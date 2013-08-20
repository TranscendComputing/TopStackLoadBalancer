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

import java.util.List;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthResponse;
import com.transcend.loadbalancer.message.LoadBalancerMessage.InstanceState;

public class DescribeInstanceHealthResultMarshaller implements
		Marshaller<String, DescribeInstanceHealthResponse> {
	// private static Logger logger = Appctx
	// .getLogger(DescribeInstanceHealthResultMarshaller.class.getName());

	@Override
	public String marshall(DescribeInstanceHealthResponse hr) {
		final XMLNode xn = new XMLNode("DescribeInstanceHealthResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", hr.getRequestId());
		final XMLNode nr = new XMLNode("DescribeInstanceHealthResult");
		xn.addNode(nr);
		final XMLNode nis = new XMLNode("InstanceState");
		nr.addNode(nis);

		final List<InstanceState> his = hr.getInstanceStateList();
		for (final InstanceState is : his) {
			final XMLNode xmem = new XMLNode("member");
			nis.addNode(xmem);
			final XMLNode nid = new XMLNode("InstanceId");
			xmem.addNode(nid);
			final XMLNode nid0 = new XMLNode();
			nid.addNode(nid0);
			nid0.setPlaintext(is.getInstanceId());
			final XMLNode nds = new XMLNode("Description");
			xmem.addNode(nds);
			final XMLNode nds0 = new XMLNode();
			nds.addNode(nds0);
			nds0.setPlaintext(is.getDescription());
			final XMLNode nrc = new XMLNode("ReasonCode");
			xmem.addNode(nrc);
			final XMLNode nrc0 = new XMLNode();
			nrc.addNode(nrc0);
			nrc0.setPlaintext(is.getReasonCode());
			final XMLNode nst = new XMLNode("State");
			xmem.addNode(nst);
			final XMLNode nst0 = new XMLNode();
			nst.addNode(nst0);
			nst0.setPlaintext(is.getState().toString());
		}
		return xn.toString();
	}
}
