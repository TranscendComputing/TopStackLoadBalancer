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

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.AppCookieStickinessPolicyType;
import com.msi.tough.cf.elasticloadbalancing.LBCookieStickinessPolicyType;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.DeleteLoadBalancerPolicyMessage.DeleteLoadBalancerPolicyRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerPolicyMessage.DeleteLoadBalancerPolicyResult;

public class DeleteLoadBalancerPolicyWorker
        extends
        AbstractWorker<DeleteLoadBalancerPolicyRequest, DeleteLoadBalancerPolicyResult> {
    private final Logger logger = Appctx
            .getLogger(DeleteLoadBalancerPolicyWorker.class.getName());

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
    public DeleteLoadBalancerPolicyResult doWork(
            DeleteLoadBalancerPolicyRequest req) throws Exception {
        logger.debug("Performing work for DeleteLoadBalancerPolicy.");
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
    protected DeleteLoadBalancerPolicyResult doWork0(
            DeleteLoadBalancerPolicyRequest req, ServiceRequestContext context)
            throws Exception {
        final AccountBean account = context.getAccountBean();
        final DeleteLoadBalancerPolicyResult.Builder ret = DeleteLoadBalancerPolicyResult
                .newBuilder();

        final String name = req.getLoadBalancerName();
        logger.debug("Operation DeleteLoadBalancerPolicy " + name);

        // find out if load balancer exists
        Session session = getSession();
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }
        String app = lbean.getAppCookieStickinessPolicy();
        if (app != null) {
            final List<AppCookieStickinessPolicyType> appList = LoadBalancerUtil
                    .toAppCookieStickinessPolicy(JsonUtil.load(app));
            Object toRemove = null;
            for (final AppCookieStickinessPolicyType p : appList) {
                if (p.getPolicyName().equals(req.getPolicyName())) {
                    toRemove = p;
                    break;
                }
            }
            if (toRemove != null) {
                appList.remove(toRemove);
                lbean.setAppCookieStickinessPolicy(JsonUtil
                        .toJsonString(appList));
            }
        }
        app = lbean.getLbCookieStickinessPolicy();
        if (app != null) {
            final List<LBCookieStickinessPolicyType> lbList = LoadBalancerUtil
                    .toLBCookieStickinessPolicy(JsonUtil.load(app));
            Object toRemove = null;
            for (final LBCookieStickinessPolicyType p : lbList) {
                if (p.getPolicyName().equals(req.getPolicyName())) {
                    toRemove = p;
                    break;
                }
            }
            if (toRemove != null) {
                lbList.remove(toRemove);
                lbean.setLbCookieStickinessPolicy(JsonUtil.toJsonString(lbList));
            }
        }
        session.save(lbean);

        final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
                session, lbean);

        final String script = LoadBalancerUtil.toJson(lbtype);
        CFUtil.runAWSScript(session, lbtype.getStackId(), account.getId(),
                script, new TemplateContext(null), true);

        return ret.buildPartial();
    }
}
