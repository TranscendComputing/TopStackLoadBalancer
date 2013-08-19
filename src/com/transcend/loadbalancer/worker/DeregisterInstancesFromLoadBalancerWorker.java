package com.transcend.loadbalancer.worker;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerRequest;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerResponse;

public class DeregisterInstancesFromLoadBalancerWorker 
	extends AbstractWorker<DeregisterInstancesFromLoadBalancerRequest, DeregisterInstancesFromLoadBalancerResponse>{
	
	private final Logger logger = Appctx.getLogger(DeregisterInstancesFromLoadBalancerWorker.class
            .getName());
	
	/**
     * We need a local copy of this doWork to provide the transactional
     * annotation. Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public DeregisterInstancesFromLoadBalancerResponse doWork(DeregisterInstancesFromLoadBalancerRequest req)
            throws Exception {
        logger.debug("Performing work for DeregisterInstancesFromLoadBalancer (1st phase).");
        return super.doWork(req, getSession());
    }
    
	@Override
	protected DeregisterInstancesFromLoadBalancerResponse doWork0(
			DeregisterInstancesFromLoadBalancerRequest r,
			ServiceRequestContext context) throws Exception {
		final DeregisterInstancesFromLoadBalancerResponse.Builder ret = DeregisterInstancesFromLoadBalancerResponse.newBuilder();
		final Session session = getSession();
		
		final String name = r.getLoadBalancerName();
		logger.debug("Operation DeregisterInstancesFromLoadBalancer " + name);

		// find out if load balancer exists
		final AccountBean ac = context.getAccountBean();
		final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
				ac.getId(), name);
		if (lbean == null) {
			throw LoadBalancerQueryFaults.loadBalancerNotFound();
		}

		final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
				session, lbean);
		final List<String> instances = lbtype.getInstances();
		for (final String in : r.getInstanceList()) {
			if (instances.contains(in)) {
				instances.remove(in);
			} else {
				throw LoadBalancerQueryFaults.invalidInstance();
			}
		}
		
		for(String in : instances){
			ret.addInstance(in);
		}
		ret.setLoadBalancerName(name);
		DeregisterInstancesFromLoadBalancerResponse result = ret.buildPartial();
		logger.debug("Response " + result.toString());
		return result;
	}

}
