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

import org.slf4j.Logger;

import com.amazonaws.services.elasticloadbalancing.model.transform.DeleteLoadBalancerPolicyRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.DeleteLoadBalancerPolicyMessage.DeleteLoadBalancerPolicyRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerPolicyMessage.DeleteLoadBalancerPolicyResult;
import com.yammer.metrics.core.Meter;

public class DeleteLoadBalancerPolicy
        extends
        AbstractQueuedAction<DeleteLoadBalancerPolicyRequest, DeleteLoadBalancerPolicyResult> {
	private final static Logger logger = Appctx
			.getLogger(DeleteLoadBalancerPolicy.class.getName());

	private static Map<String, Meter> meters = initMeter(
			"ElasticLoadbalancing", "DeleteLoadBalancerPolicy");

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DeleteLoadBalancerPolicyRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DeleteLoadBalancerPolicyRequest r =
                DeleteLoadBalancerPolicyRequestUnmarshaller
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
            DeleteLoadBalancerPolicyResult message) {
        resp.setPayload(marshall(message));
        return resp;
    }

    private String marshall(final DeleteLoadBalancerPolicyResult input) {
        final XMLNode xn = new XMLNode("DeleteLoadBalancerPolicyResponse");
        xn.addAttr("xmlns",
                "http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
        final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        QueryUtil.addNode(mmeta, "RequestId", input.getRequestId());
        return xn.toString();
    }

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}
}
