package com.transcend.loadbalancer.worker;

import java.util.ArrayList;
import java.util.List;

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
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersResult;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

public class CreateLoadBalancerListenersWorker extends
        AbstractWorker<CreateLoadBalancerListenersRequest, CreateLoadBalancerListenersResult> {
    private final Logger logger = Appctx.getLogger(CreateLoadBalancerListenersWorker.class
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
    public CreateLoadBalancerListenersResult doWork(CreateLoadBalancerListenersRequest req)
            throws Exception {
        logger.debug("Performing work for CreateLoadBalancerListeners.");
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
    protected CreateLoadBalancerListenersResult doWork0(CreateLoadBalancerListenersRequest req,
            ServiceRequestContext context) throws Exception {
        final AccountBean account = context.getAccountBean();
        final CreateLoadBalancerListenersResult.Builder ret =
                CreateLoadBalancerListenersResult.newBuilder();

        final String name = req.getLoadBalancerName();

        Session session = getSession();
        // find out if load balancer exists
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }
        create(session, account, lbean, req);
        return ret.buildPartial();
    }

    /**
    * Create LoadBalancer listener
    *
    * @param session
    * @param ac
    * @param req
    * @return
    * @throws Exception
    */
   private LoadBalancerBean create(final Session session,
           final AccountBean ac, final LoadBalancerBean lbbean,
           CreateLoadBalancerListenersRequest req)
           throws Exception {

       final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
               session, lbbean);

       // create listener records
       List<ListenerType> ls = lbtype.getListeners();
       if (ls == null) {
           ls = new ArrayList<ListenerType>();
           lbtype.setListeners(ls);
       }
       for (final Listener lsn : req.getListenerList()) {
           for (final ListenerType l : ls) {
               if (l.getLoadBalancerPort().equals(
                       "" + lsn.getLoadBalancerPort())) {
                   if (!l.getInstancePort().equals(""+lsn.getInstancePort()) ||
                           !l.getProtocol().equals(lsn.getProtocol())) {
                       throw LoadBalancerQueryFaults.duplicateListener();
                   } else {
                       return lbbean; // Identical listener.
                   }
               }
           }
           final ListenerType lsnb = new ListenerType();
           final String protocol = lsn.getProtocol().toLowerCase();
           if (!protocol.equals("http")) {
               throw LoadBalancerQueryFaults.invalidConfigurationRequest();
           }
           lsnb.setProtocol(protocol);
           String instanceProtocol = lsn.getInstanceProtocol();
           if (instanceProtocol == null) {
               instanceProtocol = protocol;
           } else {
               instanceProtocol = instanceProtocol.toLowerCase();
           }
           if (!instanceProtocol.equals("http")) {
               throw LoadBalancerQueryFaults.invalidConfigurationRequest();
           }
           lsnb.setInstancePort(instanceProtocol);
           lsnb.setInstancePort("" + lsn.getInstancePort());
           lsnb.setLoadBalancerPort("" + lsn.getLoadBalancerPort());
           lsnb.setSSLCertificateId(lsn.getSSLCertificateId());
           ls.add(lsnb);
       }

       final String script = LoadBalancerUtil.toJson(lbtype);
       CFUtil.runAWSScript(session, lbtype.getStackId(), ac.getId(), script,
               new TemplateContext(null), true);

       return lbbean;
   }
}