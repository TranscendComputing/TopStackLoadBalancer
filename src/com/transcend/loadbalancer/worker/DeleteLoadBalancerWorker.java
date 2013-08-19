package com.transcend.loadbalancer.worker;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerResponse;

public class DeleteLoadBalancerWorker extends AbstractWorker<DeleteLoadBalancerRequest, DeleteLoadBalancerResponse>{

	private final Logger logger = Appctx.getLogger(DeleteLoadBalancerWorker.class
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
    public DeleteLoadBalancerResponse doWork(DeleteLoadBalancerRequest req)
            throws Exception {
        logger.debug("Performing work for DeleteLoadBalancer.");
        return super.doWork(req, getSession());
    }


	@Override
	protected DeleteLoadBalancerResponse doWork0(DeleteLoadBalancerRequest r,
			ServiceRequestContext context) throws Exception {
		final String name = r.getLoadBalancerName();
		DeleteLoadBalancerResponse.Builder resp = DeleteLoadBalancerResponse.newBuilder();
		resp.setLoadBalancerName(name);
		logger.debug("Operation deleteLoadBalancer " + name);

		// return response and let the second phase worker handle the resource clean up
		return resp.buildPartial();
	}

}
