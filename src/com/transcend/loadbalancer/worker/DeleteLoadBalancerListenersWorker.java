package com.transcend.loadbalancer.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.ListenerType;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.DeleteLoadBalancerListenersMessage.DeleteLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerListenersMessage.DeleteLoadBalancerListenersResult;

public class DeleteLoadBalancerListenersWorker extends
        AbstractWorker<DeleteLoadBalancerListenersRequest, DeleteLoadBalancerListenersResult> {
    private final Logger logger = Appctx.getLogger(DeleteLoadBalancerListenersWorker.class
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
    public DeleteLoadBalancerListenersResult doWork(DeleteLoadBalancerListenersRequest req)
            throws Exception {
        logger.debug("Performing work for DeleteLoadBalancerListeners.");
        return super.doWork(req, getSession());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.workflow.core.AbstractWorker#doWork0(com.google.protobuf
     * .Message, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    protected DeleteLoadBalancerListenersResult doWork0(DeleteLoadBalancerListenersRequest req,
            ServiceRequestContext context) throws Exception {
        final AccountBean account = context.getAccountBean();
        final DeleteLoadBalancerListenersResult.Builder ret =
                DeleteLoadBalancerListenersResult.newBuilder();

        final String name = req.getLoadBalancerName();

        Session session = getSession();
        // find out if load balancer exists
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }

        final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
                session, lbean);

        // delete listener records
        for (final Integer i : req.getLoadBalancerPortList()) {
            for (final ListenerType lb : lbtype.getListeners()) {
                if (lb.getLoadBalancerPort().equals("" + i)) {
                    lbtype.getListeners().remove(lb);
                    break;
                }
            }
        }

        final String script = LoadBalancerUtil.toJson(lbtype);
        CFUtil.runAWSScript(session, lbtype.getStackId(), account.getId(),
                script,
                new TemplateContext(null), true);

        return ret.buildPartial();
    }
}