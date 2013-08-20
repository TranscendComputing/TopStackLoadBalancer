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
