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
package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckRequest;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckResponse;
import com.transcend.loadbalancer.message.LoadBalancerMessage.HealthCheck;
import com.yammer.metrics.core.Meter;

public class ConfigureHealthCheck
        extends
        AbstractQueuedAction<ConfigureHealthCheckRequest, ConfigureHealthCheckResponse> {

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "ConfigureHealthCheck");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    private String marshall(final ConfigureHealthCheckResponse resp) {
        final XMLNode xn = new XMLNode("ConfigureHealthCheckResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", resp.getRequestId());

        final XMLNode nr = new XMLNode("ConfigureHealthCheckResult");
        xn.addNode(nr);

        final HealthCheck hc = resp.getHealthCheck();
        final XMLNode nhc = new XMLNode("HealthCheck");
        nr.addNode(nhc);
        final XMLNode hthld = new XMLNode("HealthyThreshold");
        nhc.addNode(hthld);
        final XMLNode hthld0 = new XMLNode();
        hthld.addNode(hthld0);
        hthld0.setPlaintext("" + hc.getHealthyThreshold());
        final XMLNode intv = new XMLNode("Interval");
        nhc.addNode(intv);
        final XMLNode intv0 = new XMLNode();
        intv.addNode(intv0);
        intv0.setPlaintext("" + hc.getInterval());
        if (hc.getTarget() != null) {
            final XMLNode tgt = new XMLNode("Target");
            nhc.addNode(tgt);
            final XMLNode tgt0 = new XMLNode();
            tgt.addNode(tgt0);
            tgt0.setPlaintext(hc.getTarget());
        }
        final XMLNode tmo = new XMLNode("Timeout");
        nhc.addNode(tmo);
        final XMLNode tmo0 = new XMLNode();
        tmo.addNode(tmo0);
        tmo0.setPlaintext("" + hc.getTimeout());
        final XMLNode uh = new XMLNode("UnhealthyThreshold");
        nhc.addNode(uh);
        final XMLNode uh0 = new XMLNode();
        uh.addNode(uh0);
        uh0.setPlaintext("" + hc.getUnhealthyThreshold());
        return xn.toString();
    }

    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            ConfigureHealthCheckResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private ConfigureHealthCheckRequest unmarshall(
            final Map<String, String[]> in) throws ErrorResponse {
        try {
            final ConfigureHealthCheckRequest.Builder req = ConfigureHealthCheckRequest
                    .newBuilder();
            req.setLoadBalancerName(in.get("LoadBalancerName")[0]);
            final HealthCheck.Builder hc = HealthCheck.newBuilder();
            hc.setHealthyThreshold(QueryUtil.requiredInt(in,
                    "HealthCheck.HealthyThreshold"));
            hc.setInterval(QueryUtil.requiredInt(in, "HealthCheck.Interval"));
            hc.setTarget(QueryUtil.requiredString(in, "HealthCheck.Target"));
            hc.setTimeout(QueryUtil.requiredInt(in, "HealthCheck.Timeout"));
            hc.setUnhealthyThreshold(QueryUtil.requiredInt(in,
                    "HealthCheck.UnhealthyThreshold"));
            req.setHealthCheck(hc);
            if (hc.getHealthyThreshold() <= 0 || hc.getInterval() <= 0
                    || hc.getTimeout() <= 0 || hc.getUnhealthyThreshold() <= 0) {
                throw LoadBalancerQueryFaults.validationError();
            }
            return req.buildPartial();
        } catch (final ErrorResponse err) {
            throw err;
        } catch (final Exception e) {
            throw LoadBalancerQueryFaults.validationError();
        }
    }

    @Override
    public ConfigureHealthCheckRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        return unmarshall(req.getParameterMap());
    }
}
