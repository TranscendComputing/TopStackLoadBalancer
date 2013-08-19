package com.transcend.loadbalancer.worker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.ListenerType;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractPhaseWorker;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerResult;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

public class StandUpLoadBalancerWorker extends
    AbstractPhaseWorker<CreateLoadBalancerResult> {
    private final Logger logger = Appctx.getLogger(StandUpLoadBalancerWorker.class
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
    public void doWork(CreateLoadBalancerResult req)
            throws Exception {
        logger.debug("Performing work for StandUpLoadBalancer (step 2 of create).");
        super.doWork(req, getSession());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.workflow.core.AbstractWorker#doWork0(com.google.protobuf
     * .Message, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    protected CreateLoadBalancerResult doWork0(CreateLoadBalancerResult req,
            ServiceRequestContext context) throws Exception {
        final AccountBean account = context.getAccountBean();

        final List<ListenerType> ltypes = new ArrayList<ListenerType>();
        for (final Listener lsn : req.getListenersList()) {
            final ListenerType ltype = new ListenerType();
            ltype.setInstancePort(Integer.toString(lsn.getInstancePort()));
            ltype.setLoadBalancerPort(Integer.toString(lsn.getLoadBalancerPort()));
            final String protocol = lsn.getProtocol().toLowerCase();
            if (!protocol.equals("http")) {
                throw LoadBalancerQueryFaults.invalidConfigurationRequest();
            }
            ltype.setProtocol(protocol);
            String instanceProtocol = lsn.getInstanceProtocol();
            if (instanceProtocol == null) {
                instanceProtocol = protocol;
            }
            instanceProtocol = instanceProtocol.toLowerCase();
            if (!instanceProtocol.equals("http")) {
                throw LoadBalancerQueryFaults.invalidConfigurationRequest();
            }
            ltype.setSSLCertificateId(lsn.getSSLCertificateId());
            ltypes.add(ltype);
        }
        // create load balancer type
        final LoadBalancerType lbtype = new LoadBalancerType();
        lbtype.setAcId(account.getId());
        final CommaObject avzo = new CommaObject(req.getAvailabilityZoneList());
        lbtype.setAvailabilityZones(avzo.toString());
        // lbtype.setAppCookieStickinessPolicy(appCookieStickinessPolicy);
        // lbtype.setDatabag(databag);
        // lbtype.setDnsName(dnsName);
        // lbtype.setHealthCheck(healthCheck);
        // lbtype.setInstances(instances);
        // lbtype.setLbCookieStickinessPolicy(lbCookieStickinessPolicy);
        lbtype.setListeners(ltypes);
        lbtype.setName(req.getLoadBalancerName());
        // lbtype.setSecurityGroups(securityGroups);
        // lbtype.setStackId(stackId);
        // lbtype.setSubnets(subnets);

        final String script = LoadBalancerUtil.toJson(lbtype);
        logger.debug("Executing startup of load balancer: "+req.getLoadBalancerName());
        CFUtil.runAWSScript(getSession(),
                req.getStackId(), account.getId(), script,
                new TemplateContext(null), false);
        logger.debug("Executed startup of load balancer: "+req.getLoadBalancerName());
        return req;
    }
}