package com.transcend.loadbalancer.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.HealthCheckType;
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
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckRequest;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckResponse;
import com.transcend.loadbalancer.message.LoadBalancerMessage.HealthCheck;

public class ConfigureHealthCheckWorker
        extends
        AbstractWorker<ConfigureHealthCheckRequest, ConfigureHealthCheckResponse> {

    private final Logger logger = Appctx
            .getLogger(ConfigureHealthCheckWorker.class.getName());

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
    public ConfigureHealthCheckResponse doWork(ConfigureHealthCheckRequest req)
            throws Exception {
        logger.debug("Performing work for ConfigureHealthCheck.");
        return super.doWork(req, getSession());
    }

    @Override
    protected ConfigureHealthCheckResponse doWork0(
            ConfigureHealthCheckRequest req, ServiceRequestContext context)
            throws Exception {
        // TODO Auto-generated method stub
        final ConfigureHealthCheckResponse.Builder ret = ConfigureHealthCheckResponse
                .newBuilder();
        final String name = req.getLoadBalancerName();
        logger.debug("Operation ConfigureHealthCheck " + name);

        Session session = getSession();
        // find out if load balancer is already created
        final AccountBean account = context.getAccountBean();
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }

        final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
                session, lbean);

        final HealthCheck hc = req.getHealthCheck();
        HealthCheckType hct = lbtype.getHealthCheck();
        if (hct == null) {
            hct = new HealthCheckType();
            lbtype.setHealthCheck(hct);
        }
        hct.setHealthyThreshold("" + hc.getHealthyThreshold());
        hct.setInterval("" + hc.getInterval());
        hct.setTarget(hc.getTarget());
        hct.setTimeout("" + hc.getTimeout());
        hct.setUnhealthyThreshold("" + hc.getUnhealthyThreshold());
        lbtype.setHealthCheck(hct);
        // lbean.setHealthyThreshold(hc.getHealthyThreshold());
        // lbean.setUnhealthyThreshold(hc.getUnhealthyThreshold());
        // lbean.setInterval(hc.getInterval());
        // lbean.setTimeout(hc.getTimeout());
        // lbean.setTarget(hc.getTarget());
        // session.save(lbean);

        final String script = LoadBalancerUtil.toJson(lbtype);
        CFUtil.runAWSScript(session, lbtype.getStackId(), account.getId(), script,
                new TemplateContext(null), true);

        ret.setHealthCheck(hc);
        logger.debug("Response " + ret);
        return ret.buildPartial();
    }
}
