package com.transcend.loadbalancer.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractPhaseWorker;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerResponse;

public class ShutDownLoadBalancerWorker extends
	AbstractPhaseWorker<DeleteLoadBalancerResponse>{

	private final Logger logger = Appctx.getLogger(ShutDownLoadBalancerWorker.class
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
    public void doWork(DeleteLoadBalancerResponse req)
            throws Exception {
        logger.debug("Performing work for DeleteLoadBalancer.");
        super.doWork(req, getSession());
    }
	
	@Override
	protected DeleteLoadBalancerResponse doWork0(
			DeleteLoadBalancerResponse req, ServiceRequestContext context)
			throws Exception {
		
		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final String name = req.getLoadBalancerName();
		final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
				ac.getId(), name);
		
		logger.debug("DeleteLB request: AccountId = " + ac.getId() + "; LB: " + name);
		
		if (lbean == null) {
			logger.debug("It looks like the target LB does not exist. Exit and return.");
			return req;
		}

		if (lbean.getStackId() != null) {
			CFUtil.deleteStackResources(AccountUtil.toAccount(ac),
					lbean.getStackId(), null, null);
			/*final LoadBalancerBean lb = LoadBalancerUtil.read(session,
					ac.getId(), name);
			LoadBalancerUtil.deleteLoadBalancer(session, ac.getId(), lb);*/
		} else {
			final LoadBalancerBean lb = LoadBalancerUtil.read(session,
					ac.getId(), name);
			if (lb != null) {
				LoadBalancerUtil.deleteLoadBalancer(session, ac.getId(), lb);
			}
		}
		
		return req;
	}

}
