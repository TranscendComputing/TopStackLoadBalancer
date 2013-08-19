package com.msi.tough.query.elasticloadbalancing;

import com.amazonaws.services.elasticloadbalancing.model.transform.CreateLoadBalancerRequestUnmarshaller;
import com.amazonaws.services.elasticloadbalancing.model.transform.CreateLoadBalancerResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerRequest;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerResult;

/**
 * Create LoadBalancer action
 *
 * @author raj
 *
 */
public class CreateLoadBalancer extends
        AbstractQueuedAction
        <CreateLoadBalancerRequest, CreateLoadBalancerResult> {

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public CreateLoadBalancerRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final CreateLoadBalancerRequest r = CreateLoadBalancerRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }

    /* (non-Javadoc)
     * @see com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            CreateLoadBalancerResult message) {
        resp.setPayload(new CreateLoadBalancerResultMarshaller().marshall(message));
        return resp;
    }
}
