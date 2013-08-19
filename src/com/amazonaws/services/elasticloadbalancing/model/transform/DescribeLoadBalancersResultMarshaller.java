/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.services.elasticloadbalancing.model.transform;

import org.slf4j.Logger;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.AppCookieStickinessPolicy;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersResult;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.LBCookieStickinessPolicy;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.ListenerDescription;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.LoadBalancerDescription;
import com.transcend.loadbalancer.message.LoadBalancerMessage.HealthCheck;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Instance;

public class DescribeLoadBalancersResultMarshaller {
	private static Logger logger = Appctx
			.getLogger(DescribeLoadBalancersResultMarshaller.class.getName());

	public String marshall(DescribeLoadBalancersResult res) {
		final XMLNode xn = new XMLNode("DescribeLoadBalancersResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", res.getRequestId());
		final XMLNode nr = QueryUtil.addNode(xn, "DescribeLoadBalancersResult");
		final XMLNode ldescs = QueryUtil
				.addNode(nr, "LoadBalancerDescriptions");

		for (final LoadBalancerDescription el : res
				.getLoadBalancerDescriptionsList()) {
			final XMLNode xmem = QueryUtil.addNode(ldescs, "member");
			QueryUtil.addNode(xmem, "LoadBalancerName",
					el.getLoadBalancerName());

			if (el.getAvailabilityZonesCount() > 0) {
				final XMLNode avzs = QueryUtil.addNode(xmem,
						"AvailabilityZones");
				for (final String i : el.getAvailabilityZonesList()) {
					QueryUtil.addNode(avzs, "member", i);
				}
			}
			QueryUtil.addNode(xmem, "CreatedTime", el.getCreatedTime());
			QueryUtil.addNode(xmem, "DNSName", el.getDnsName());

			final XMLNode inst = QueryUtil.addNode(xmem, "Instances");
			for (final Instance i : el.getInstancesList()) {
				logger.debug("Marshall instance " + i.getInstanceId());
				final XMLNode instm = QueryUtil.addNode(inst, "member");
				QueryUtil.addNode(instm, "InstanceId", i.getInstanceId());
			}

			final HealthCheck hco = el.getHealthCheck();
			if (hco != null && hco.getTarget() != null) {
				final XMLNode hc = QueryUtil.addNode(xmem, "HealthCheck");
				QueryUtil.addNode(hc, "Interval", hco.getInterval());
				QueryUtil.addNode(hc, "Target", hco.getTarget());
				QueryUtil.addNode(hc, "HealthyThreshold",
						hco.getHealthyThreshold());
				QueryUtil.addNode(hc, "UnhealthyThreshold",
						hco.getUnhealthyThreshold());
				QueryUtil.addNode(hc, "Timeout", hco.getTimeout());
			}

			if (el.getListenerDescriptionList() != null) {
				final XMLNode ld = QueryUtil.addNode(xmem,
						"ListenerDescriptions");
				for (final ListenerDescription l : el.getListenerDescriptionList()) {
					final XMLNode lm = QueryUtil.addNode(ld, "member");
					final XMLNode ll = QueryUtil.addNode(lm, "Listener");
					QueryUtil.addNode(ll, "Protocol", l.getListener()
							.getProtocol());
					QueryUtil.addNode(ll, "LoadBalancerPort", l.getListener()
							.getLoadBalancerPort());
					QueryUtil.addNode(ll, "InstancePort", l.getListener()
							.getInstancePort());

					final XMLNode pns = QueryUtil.addNode(lm, "PolicyNames");
					for (final String pn : l.getPolicyNamesList()) {
						QueryUtil.addNode(pns, "member", pn);
					}
				}
			}

			if (el.hasPolicies()) {
				final XMLNode lmps = QueryUtil.addNode(xmem, "Policies");
				if (el.getPolicies().getAppCookieStickinessPoliciesCount() > 0) {
					final XMLNode ps = QueryUtil.addNode(lmps,
							"AppCookieStickinessPolicies");
					for (final AppCookieStickinessPolicy p : el.getPolicies()
							.getAppCookieStickinessPoliciesList()) {
						logger.debug("AppCookieStickinessPolicy " + p);
						final XMLNode m = new XMLNode("member");
						ps.addNode(m);
						if (p.hasCookieName()) {
							QueryUtil.addNode(m, "CookieName",
									p.getCookieName());
						}
						if (p.hasPolicyName()) {
							QueryUtil.addNode(m, "PolicyName",
									p.getPolicyName());
						}
					}
				}
				if (el.getPolicies().getLBCookieStickinessPoliciesCount() > 0) {
					final XMLNode ps = QueryUtil.addNode(lmps,
							"LBCookieStickinessPolicies");
					for (final LBCookieStickinessPolicy p : el.getPolicies()
							.getLBCookieStickinessPoliciesList()) {
						logger.debug("LBCookieStickinessPolicy " + p);
						final XMLNode m = QueryUtil.addNode(ps, "member");
						if (p.hasCookieExpirationPeriod()) {
							QueryUtil.addNode(m, "CookieExpirationPeriod",
									p.getCookieExpirationPeriod());
						}
						if (p.hasPolicyName()) {
							QueryUtil.addNode(m, "PolicyName",
									p.getPolicyName());
						}
					}
				}
			}
		}
		return xn.toString();
	}
}
