package com.transcend.loadbalancer.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.AppCookieStickinessPolicyType;
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
import com.transcend.loadbalancer.message.CreateAppCookieStickinessPolicyMessage.CreateAppCookieStickinessPolicyRequest;
import com.transcend.loadbalancer.message.CreateAppCookieStickinessPolicyMessage.CreateAppCookieStickinessPolicyResponse;
import com.yammer.metrics.core.Meter;

public class CreateAppCookieStickinessPolicyWorker
        extends
        AbstractWorker<CreateAppCookieStickinessPolicyRequest, CreateAppCookieStickinessPolicyResponse> {

    private final static Logger logger = Appctx
            .getLogger(CreateAppCookieStickinessPolicyWorker.class.getName());

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "CreateAppCookieStickinessPolicy");

    @Override
    protected void mark(CreateAppCookieStickinessPolicyResponse ret, Exception e) {
        markStandard(meters, e);
    }

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
    public CreateAppCookieStickinessPolicyResponse doWork(
            CreateAppCookieStickinessPolicyRequest req) throws Exception {
        logger.debug("Performing work for CreateAppCookieStickinessPolicy.");
        return super.doWork(req, getSession());
    }

    @Override
    protected CreateAppCookieStickinessPolicyResponse doWork0(
            CreateAppCookieStickinessPolicyRequest req,
            ServiceRequestContext context) throws Exception {
        final CreateAppCookieStickinessPolicyResponse.Builder ret =
                CreateAppCookieStickinessPolicyResponse
                .newBuilder();
        final String name = req.getLoadBalancerName();

        Session session = getSession();
        final AccountBean account = context.getAccountBean();
        // find out if load balancer exists
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }

        final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
                session, lbean);

        List<AppCookieStickinessPolicyType> pl = lbtype
                .getAppCookieStickinessPolicy();
        if (pl == null || pl.size() == 0) {
            pl = new ArrayList<AppCookieStickinessPolicyType>();
            lbtype.setAppCookieStickinessPolicy(pl);
        }
        final AppCookieStickinessPolicyType p = new AppCookieStickinessPolicyType();
        p.setCookieName(req.getCookieName());
        p.setPolicyName(req.getPolicyName());
        for (final AppCookieStickinessPolicyType pol : pl) {
            if (pol.getPolicyName().equals(p.getPolicyName())) {
                throw LoadBalancerQueryFaults.duplicatePolicyName();
            }
        }
        pl.add(p);

        final String script = LoadBalancerUtil.toJson(lbtype);
        CFUtil.runAWSScript(session, lbtype.getStackId(), account.getId(),
                script, new TemplateContext(null), true);
        return ret.buildPartial();
    }
}
