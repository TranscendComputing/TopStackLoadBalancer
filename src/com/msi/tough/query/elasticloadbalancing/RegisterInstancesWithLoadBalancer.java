package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerRequest;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerResponse;
import com.yammer.metrics.core.Meter;

public class RegisterInstancesWithLoadBalancer
        extends
        AbstractQueuedAction<RegisterInstancesWithLoadBalancerRequest, RegisterInstancesWithLoadBalancerResponse> {

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "RegisterInstancesWithLoadBalancer");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            RegisterInstancesWithLoadBalancerResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    @Override
    public RegisterInstancesWithLoadBalancerRequest handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        return unmarshall(req.getParameterMap());
    }

    private RegisterInstancesWithLoadBalancerRequest unmarshall(
            final Map<String, String[]> in) throws ErrorResponse {
        final RegisterInstancesWithLoadBalancerRequest.Builder req = RegisterInstancesWithLoadBalancerRequest
                .newBuilder();
        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));
        int i = 0;
        while (true) {
            i++;
            final String s = QueryUtil.getString(in, "Instances.member." + i
                    + ".InstanceId");
            if (s == null) {
                break;
            }
            req.addInstance(s);
        }
        return req.buildPartial();
    }

    private String marshall(final RegisterInstancesWithLoadBalancerResponse resp) {
        final XMLNode xn = new XMLNode(
                "RegisterInstancesWithLoadBalancerResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", resp.getRequestId());
        final XMLNode nr = new XMLNode(
                "RegisterInstancesWithLoadBalancerResult");
        xn.addNode(nr);
        final XMLNode ni = new XMLNode("Instances");
        nr.addNode(ni);
        for (final String ins : resp.getInstanceList()) {
            final XMLNode m = new XMLNode("member");
            ni.addNode(m);
            final XMLNode id = new XMLNode("InstanceId");
            m.addNode(id);
            final XMLNode id0 = new XMLNode();
            id.addNode(id0);
            id0.setPlaintext(ins);
        }
        return xn.toString();
    }
}
