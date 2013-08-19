package com.msi.tough.query.elasticloadbalancing;

import com.amazonaws.services.elasticloadbalancing.model.transform.CreateLBCookieStickinessPolicyRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.CreateLBCookieStickinessPolicyMessage.CreateLBCookieStickinessPolicyRequest;
import com.transcend.loadbalancer.message.CreateLBCookieStickinessPolicyMessage.CreateLBCookieStickinessPolicyResponse;

public class CreateLBCookieStickinessPolicy
        extends
        AbstractQueuedAction<CreateLBCookieStickinessPolicyRequest, CreateLBCookieStickinessPolicyResponse> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public CreateLBCookieStickinessPolicyRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final CreateLBCookieStickinessPolicyRequest r = CreateLBCookieStickinessPolicyRequestUnmarshaller
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
            CreateLBCookieStickinessPolicyResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(CreateLBCookieStickinessPolicyResponse input) {
        final XMLNode xn = new XMLNode("CreateLBCookieStickinessPolicyResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", input.getRequestId());
        return xn.toString();
    }
}
