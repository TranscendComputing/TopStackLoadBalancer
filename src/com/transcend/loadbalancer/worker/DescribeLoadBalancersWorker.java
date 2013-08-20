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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.DateUtils;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ListenerBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.InstanceUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.AppCookieStickinessPolicy;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersRequest;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersResult;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.LBCookieStickinessPolicy;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.ListenerDescription;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.LoadBalancerDescription;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.Policies;
import com.transcend.loadbalancer.message.LoadBalancerMessage.HealthCheck;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Instance;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;
import com.yammer.metrics.core.Meter;

public class DescribeLoadBalancersWorker extends
    AbstractWorker<DescribeLoadBalancersRequest, DescribeLoadBalancersResult> {
    private final Logger logger = Appctx.getLogger(DescribeLoadBalancersWorker.class
    .getName());

    private DateUtils dateUtils = new DateUtils();

	private static Map<String, Meter> meters = initMeter(
			"ElasticLoadbalancing", "DescribeLoadBalancers");

	protected void mark(DescribeLoadBalancersResult ret, Exception e) {
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
    public DescribeLoadBalancersResult doWork(DescribeLoadBalancersRequest req)
            throws Exception {
        logger.debug("Performing work for DescribeLoadBalancers.");
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
    protected DescribeLoadBalancersResult doWork0(DescribeLoadBalancersRequest req,
            ServiceRequestContext context) throws Exception {
        final AccountBean account = context.getAccountBean();
        final DescribeLoadBalancersResult.Builder ret =
                DescribeLoadBalancersResult.newBuilder();
        boolean all = false;
        if (req.getLoadBalancerNamesCount() == 0) {
            all = true;
        }
        Session session = getSession();
        final Query q = session
                .createQuery("from LoadBalancerBean where userId=" +
                        account.getId());
        @SuppressWarnings("unchecked")
        final List<LoadBalancerBean> loadBalancers = q.list();
        int cnt = 0;
        for (final LoadBalancerBean lbean : loadBalancers) {
            if (lbean.getLbStatus().equalsIgnoreCase("deleting")) {
                continue;
            }
            if (!all) {
                boolean cont = false;
                for (final String name : req.getLoadBalancerNamesList()) {
                    if (name.equals(lbean.getLoadBalancerName())) {
                        cont = true;
                        break;
                    }
                }
                if (!cont) {
                    continue;
                }
            }
            final LoadBalancerDescription.Builder ld =
                    LoadBalancerDescription.newBuilder();
            final List<String> availabilityZones = new ArrayList<String>();
            if (lbean.getAvzones() != null) {
                for (final String b : lbean.getAvzones().split(",")) {
                    if (b.length() > 0) {
                        availabilityZones.add(b);
                    }
                }
                ld.addAllAvailabilityZones(availabilityZones);
            }

            final Date createdTime = lbean.getCreatedTime();
            ld.setCreatedTime(dateUtils.formatIso8601Date(createdTime));

            ld.setDnsName(lbean.getDnsName());

            if (lbean.getTarget() != null) {
                final HealthCheck.Builder healthCheck =
                        HealthCheck.newBuilder();
                healthCheck.setHealthyThreshold((int) lbean
                        .getHealthyThreshold());
                healthCheck.setInterval((int) lbean.getInterval());
                healthCheck.setTarget(lbean.getTarget());
                healthCheck.setTimeout((int) lbean.getTimeout());
                healthCheck.setUnhealthyThreshold((int) lbean
                        .getUnhealthyThreshold());
                ld.setHealthCheck(healthCheck);
            }

            if (lbean.getInstances() != null) {
                final CommaObject coi = new CommaObject(lbean.getInstances());
                for (final String ibs : coi.toList()) {
                    if (ibs == null || ibs.length() == 0) {
                        continue;
                    }
                    final String id = InstanceUtil
                            .endpointBasedId(session, ibs);
                    final Instance.Builder i = Instance.newBuilder();
                    logger.debug("LD instance " + ibs);
                    i.setInstanceId(id);
                    ld.addInstances(i);
                }
            }

            if (lbean.getListeners() != null) {
                for (final ListenerBean lb : lbean.getListeners()) {
                    final ListenerDescription.Builder ld0 =
                            ListenerDescription.newBuilder();
                    final Listener.Builder listener = Listener.newBuilder();
                    listener.setInstancePort((int) lb.getInstancePort());
                    listener.setLoadBalancerPort((int) lb.getLoadBalancerPort());
                    listener.setProtocol(lb.getProtocol());
                    if (lb.getSslCertificateId() != null) {
                        listener.setSSLCertificateId(lb.getSslCertificateId());
                    }
                    ld0.setListener(listener);
                    if (lb.getPolicyNames() != null) {
                        final String[] pnms = lb.getPolicyNames().split(",");
                        final List<String> policyNames = new ArrayList<String>();
                        for (final String s : pnms) {
                            policyNames.add(s);
                        }
                        ld0.addAllPolicyNames(policyNames);
                    }
                    ld.addListenerDescription(ld0);
                }
            }

            ld.setLoadBalancerName(lbean.getLoadBalancerName());

            final Policies.Builder policies = Policies.newBuilder();
            if (lbean.getAppCookieStickinessPolicy() != null) {
                final String pol = lbean.getAppCookieStickinessPolicy();
                final ArrayNode jpol = (ArrayNode) JsonUtil.load(pol);
                for (int i = 0; i < jpol.size(); i++) {
                    final AppCookieStickinessPolicy.Builder acp =
                            AppCookieStickinessPolicy.newBuilder();
                    final JsonNode node = jpol.get(i);
                    acp.setCookieName(node.get("CookieName").getTextValue());
                    acp.setPolicyName(node.get("PolicyName").getTextValue());
                    policies.addAppCookieStickinessPolicies(acp);
                }
                logger.debug("appCookieStickinessPolicies "
                        + policies.getAppCookieStickinessPoliciesCount());
            }
            if (lbean.getLbCookieStickinessPolicy() != null) {
                final String pol = lbean.getLbCookieStickinessPolicy();
                final JsonNode jpol = JsonUtil.load(pol);
                for (int i = 0; i < jpol.size(); i++) {
                    final LBCookieStickinessPolicy.Builder lbp =
                            LBCookieStickinessPolicy.newBuilder();
                    final JsonNode node = jpol.get(i);
                    if (node.get("CookieExpirationPeriod") != null) {
                        lbp.setCookieExpirationPeriod(Long.parseLong(node.get(
                                "CookieExpirationPeriod").getTextValue()));
                    }
                    lbp.setPolicyName(node.get("PolicyName").getTextValue());
                    policies.addLBCookieStickinessPolicies(lbp);
                }
                logger.debug("lBCookieStickinessPolicies "
                        + policies.getLBCookieStickinessPoliciesCount());
            }
            ld.setPolicies(policies);
            ret.addLoadBalancerDescriptions(ld);
            cnt++;
        }
        if (!all && cnt == 0) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }
        return ret.buildPartial();
    }
}
