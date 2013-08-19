package com.msi.tough.query.elasticloadbalancing;

import com.amazonaws.services.elasticloadbalancing.model.transform.CreateAppCookieStickinessPolicyRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.CreateAppCookieStickinessPolicyMessage.CreateAppCookieStickinessPolicyRequest;
import com.transcend.loadbalancer.message.CreateAppCookieStickinessPolicyMessage.CreateAppCookieStickinessPolicyResponse;

public class CreateAppCookieStickinessPolicy
        extends
        AbstractQueuedAction<CreateAppCookieStickinessPolicyRequest, CreateAppCookieStickinessPolicyResponse> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public CreateAppCookieStickinessPolicyRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final CreateAppCookieStickinessPolicyRequest r = CreateAppCookieStickinessPolicyRequestUnmarshaller
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
            CreateAppCookieStickinessPolicyResponse message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(CreateAppCookieStickinessPolicyResponse input) {
        final XMLNode xn = new XMLNode("CreateAppCookieStickinessPolicyResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", input.getRequestId());
        return xn.toString();
    }
}
