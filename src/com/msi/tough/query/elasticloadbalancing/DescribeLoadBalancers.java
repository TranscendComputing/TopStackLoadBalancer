package com.msi.tough.query.elasticloadbalancing;

import com.amazonaws.services.elasticloadbalancing.model.transform.DescribeLoadBalancersRequestUnmarshaller;
import com.amazonaws.services.elasticloadbalancing.model.transform.DescribeLoadBalancersResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersRequest;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersResult;

public class DescribeLoadBalancers
        extends
        AbstractQueuedAction<DescribeLoadBalancersRequest, DescribeLoadBalancersResult> {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DescribeLoadBalancersRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DescribeLoadBalancersRequest r = DescribeLoadBalancersRequestUnmarshaller
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
            DescribeLoadBalancersResult message) {
        resp.setPayload(new DescribeLoadBalancersResultMarshaller()
                .marshall(message));
        return resp;
    }
}
