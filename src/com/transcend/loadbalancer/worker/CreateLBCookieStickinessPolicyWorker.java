/*
 * TopStack (c) Copyright 2012-2013 Transcend Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.transcend.loadbalancer.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.LBCookieStickinessPolicyType;
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
import com.transcend.loadbalancer.message.CreateLBCookieStickinessPolicyMessage.CreateLBCookieStickinessPolicyRequest;
import com.transcend.loadbalancer.message.CreateLBCookieStickinessPolicyMessage.CreateLBCookieStickinessPolicyResponse;
import com.yammer.metrics.core.Meter;

public class CreateLBCookieStickinessPolicyWorker
        extends
        AbstractWorker<CreateLBCookieStickinessPolicyRequest, CreateLBCookieStickinessPolicyResponse> {

    private final static Logger logger = Appctx
            .getLogger(CreateLBCookieStickinessPolicyWorker.class.getName());

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "CreateLBCookieStickinessPolicy");

    @Override
    protected void mark(CreateLBCookieStickinessPolicyResponse ret, Exception e) {
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
    public CreateLBCookieStickinessPolicyResponse doWork(
            CreateLBCookieStickinessPolicyRequest req) throws Exception {
        logger.debug("Performing work for CreateLBCookieStickinessPolicy.");
        return super.doWork(req, getSession());
    }

    @Override
    protected CreateLBCookieStickinessPolicyResponse doWork0(
            CreateLBCookieStickinessPolicyRequest req,
            ServiceRequestContext context) throws Exception {
        final CreateLBCookieStickinessPolicyResponse.Builder ret =
                CreateLBCookieStickinessPolicyResponse
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

        List<LBCookieStickinessPolicyType> pl = lbtype
                .getLbCookieStickinessPolicy();
        if (pl == null || pl.size() == 0) {
            pl = new ArrayList<LBCookieStickinessPolicyType>();
            lbtype.setLbCookieStickinessPolicy(pl);
        }
        final LBCookieStickinessPolicyType p = new LBCookieStickinessPolicyType();
        p.setPolicyName(req.getPolicyName());
        p.setCookieExpirationPeriod("" + req.getCookieExpirationPeriod());
        for (final LBCookieStickinessPolicyType pol : pl) {
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
