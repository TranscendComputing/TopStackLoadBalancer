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
package com.amazonaws.services.elasticloadbalancing.model.transform;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerResult;

/**
 * Create Load Balancer Request Marshaller
 */
public class CreateLoadBalancerResultMarshaller {

	public String marshall(CreateLoadBalancerResult in)
			throws ErrorResponse {
		final XMLNode xn = new XMLNode("CreateLoadBalancerResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", in.getRequestId());
		final XMLNode nr = new XMLNode("CreateLoadBalancerResult");
		xn.addNode(nr);
		final XMLNode ndns = new XMLNode("DNSName");
		nr.addNode(ndns);
		final XMLNode ndns0 = new XMLNode();
		ndns.addNode(ndns0);
		ndns0.setPlaintext(in.getDNSName());
		System.out.println("DNSName " + in.getDNSName());
		return xn.toString();
	}
}
