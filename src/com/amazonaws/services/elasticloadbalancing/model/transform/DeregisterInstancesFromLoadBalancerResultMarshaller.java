/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.elasticloadbalancing.model.transform;

import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;

public class DeregisterInstancesFromLoadBalancerResultMarshaller
		implements
		Marshaller<String, MarshallStruct<DeregisterInstancesFromLoadBalancerResult>> {

	@Override
	public String marshall(
			final MarshallStruct<DeregisterInstancesFromLoadBalancerResult> in)
			throws Exception {
		final XMLNode xn = new XMLNode(
				"DeregisterInstancesFromLoadBalancerResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", in.getRequestId());
		final XMLNode nr = new XMLNode(
				"DeregisterInstancesFromLoadBalancerResult");
		xn.addNode(nr);
		final XMLNode ni = new XMLNode("Instances");
		nr.addNode(ni);
		for (final Instance ins : in.getMainObject().getInstances()) {
			final XMLNode m = new XMLNode("member");
			ni.addNode(m);
			final XMLNode id = new XMLNode("InstanceId");
			m.addNode(id);
			final XMLNode id0 = new XMLNode();
			id.addNode(id0);
			id0.setPlaintext(ins.getInstanceId());
		}
		return xn.toString();
	}
}
