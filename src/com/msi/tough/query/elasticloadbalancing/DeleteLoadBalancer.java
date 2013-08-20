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

import com.amazonaws.services.elasticloadbalancing.model.transform.DeleteLoadBalancerRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerResponse;
import com.yammer.metrics.core.Meter;

public class DeleteLoadBalancer extends AbstractQueuedAction<DeleteLoadBalancerRequest, DeleteLoadBalancerResponse> {
	private final static Logger logger = Appctx
			.getLogger(DeleteLoadBalancer.class.getName());

	private static Map<String, Meter> meters = initMeter(
			"ElasticLoadbalancing", "DeleteLoadBalancer");

	@Override
	public ServiceResponse buildResponse(ServiceResponse resp,
			DeleteLoadBalancerResponse message) {
		resp.setPayload(marshall(message));
        return resp;
	}

	@Override
	public DeleteLoadBalancerRequest handleRequest(ServiceRequest req,
			ServiceRequestContext context) throws ErrorResponse {
		final DeleteLoadBalancerRequest requestMessage = DeleteLoadBalancerRequestUnmarshaller
				.getInstance().unmarshall(req.getParameterMap());
		return requestMessage;
	}
	
	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	private String marshall(DeleteLoadBalancerResponse message) {
		final XMLNode xn = new XMLNode("DeleteLoadBalancerResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", message.getRequestId());
		final String resultXml = xn.toString();
		logger.debug("Result XML - DELETE LB: " + resultXml);
		return resultXml;
	}
}
