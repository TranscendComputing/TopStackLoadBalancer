package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerRequest;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerResponse;
import com.yammer.metrics.core.Meter;

public class DeregisterInstancesFromLoadBalancer
        extends
        AbstractQueuedAction<DeregisterInstancesFromLoadBalancerRequest, DeregisterInstancesFromLoadBalancerResponse> {

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "DeregisterInstancesFromLoadBalancer");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    private String marshall(
            final DeregisterInstancesFromLoadBalancerResponse resp) {
        final XMLNode xn = new XMLNode(
                "DeregisterInstancesFromLoadBalancerResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", resp.getRequestId());
        final XMLNode nr = new XMLNode(
                "DeregisterInstancesFromLoadBalancerResult");
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

    private DeregisterInstancesFromLoadBalancerRequest unmarshall(
            final Map<String, String[]> in) throws ErrorResponse {
        final DeregisterInstancesFromLoadBalancerRequest.Builder req = DeregisterInstancesFromLoadBalancerRequest
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

    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DeregisterInstancesFromLoadBalancerResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    @Override
    public DeregisterInstancesFromLoadBalancerRequest handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        return unmarshall(req.getParameterMap());
    }
}
