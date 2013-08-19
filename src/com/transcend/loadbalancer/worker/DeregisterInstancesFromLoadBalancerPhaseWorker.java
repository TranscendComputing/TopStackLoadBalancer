package com.transcend.loadbalancer.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractPhaseWorker;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerResponse;

public class DeregisterInstancesFromLoadBalancerPhaseWorker extends AbstractPhaseWorker<DeregisterInstancesFromLoadBalancerResponse>{

	private final Logger logger = Appctx.getLogger(DeregisterInstancesFromLoadBalancerPhaseWorker.class
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
    public void doWork(DeregisterInstancesFromLoadBalancerResponse req)
            throws Exception {
        logger.debug("Performing work for DeregisterInstancesFromLoadBalancer (2nd phase).");
        super.doWork(req, getSession());
    }
	
	@Override
	protected DeregisterInstancesFromLoadBalancerResponse doWork0(
			DeregisterInstancesFromLoadBalancerResponse req,
			ServiceRequestContext context) throws Exception {
		final Session session = getSession();
		final String name = req.getLoadBalancerName();
		final AccountBean ac = context.getAccountBean();
		final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
				ac.getId(), name);
		final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
				session, lbean);
		lbtype.setInstances(req.getInstanceList());
		final String script = LoadBalancerUtil.toJson(lbtype);
		CFUtil.runAWSScript(lbtype.getStackId(), ac.getId(), script,
				new TemplateContext(null));
		return req;
	}

}
