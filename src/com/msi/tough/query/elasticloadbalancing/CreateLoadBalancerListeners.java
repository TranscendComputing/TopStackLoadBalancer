package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import com.amazonaws.services.elasticloadbalancing.model.transform.CreateLoadBalancerListenersRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersResult;
import com.yammer.metrics.core.Meter;

public class CreateLoadBalancerListeners
        extends
        AbstractQueuedAction<CreateLoadBalancerListenersRequest, CreateLoadBalancerListenersResult> {

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "CreateLoadBalancerListenersAc");

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public CreateLoadBalancerListenersRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final CreateLoadBalancerListenersRequest r = CreateLoadBalancerListenersRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.
     * query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            CreateLoadBalancerListenersResult message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    private String marshall(final CreateLoadBalancerListenersResult input) {
        final XMLNode xn = new XMLNode("CreateLoadBalancerListenersResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", input.getRequestId());
        return xn.toString();
    }
}
