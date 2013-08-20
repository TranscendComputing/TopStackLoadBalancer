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

import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;

/**
 * ConfigureHealthCheckResultMarshaller
 */
public class ConfigureHealthCheckResultMarshaller implements
		Marshaller<String, MarshallStruct<ConfigureHealthCheckResult>> {

	@Override
	public String marshall(final MarshallStruct<ConfigureHealthCheckResult> in)
			throws Exception {
		final XMLNode xn = new XMLNode("ConfigureHealthCheckResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", in.getRequestId());

		final XMLNode nr = new XMLNode("ConfigureHealthCheckResult");
		xn.addNode(nr);

		final HealthCheck hc = in.getMainObject().getHealthCheck();
		final XMLNode nhc = new XMLNode("HealthCheck");
		nr.addNode(nhc);
		final XMLNode hthld = new XMLNode("HealthyThreshold");
		nhc.addNode(hthld);
		final XMLNode hthld0 = new XMLNode();
		hthld.addNode(hthld0);
		hthld0.setPlaintext("" + hc.getHealthyThreshold());
		final XMLNode intv = new XMLNode("Interval");
		nhc.addNode(intv);
		final XMLNode intv0 = new XMLNode();
		intv.addNode(intv0);
		intv0.setPlaintext("" + hc.getInterval());
		if (hc.getTarget() != null) {
			final XMLNode tgt = new XMLNode("Target");
			nhc.addNode(tgt);
			final XMLNode tgt0 = new XMLNode();
			tgt.addNode(tgt0);
			tgt0.setPlaintext(hc.getTarget());
		}
		final XMLNode tmo = new XMLNode("Timeout");
		nhc.addNode(tmo);
		final XMLNode tmo0 = new XMLNode();
		tmo.addNode(tmo0);
		tmo0.setPlaintext("" + hc.getTimeout());
		final XMLNode uh = new XMLNode("UnhealthyThreshold");
		nhc.addNode(uh);
		final XMLNode uh0 = new XMLNode();
		uh.addNode(uh0);
		uh0.setPlaintext("" + hc.getUnhealthyThreshold());
		return xn.toString();
	}
}
