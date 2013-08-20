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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class SetLoadBalancerPoliciesOfListenerRequestUnmarshaller
		implements
		Unmarshaller<SetLoadBalancerPoliciesOfListenerRequest, Map<String, String[]>> {
	// private static Logger logger = Appctx
	// .getLogger(CreateLoadBalancerListenersRequestUnmarshaller.class
	// .getName());

	private static SetLoadBalancerPoliciesOfListenerRequestUnmarshaller instance;

	public static SetLoadBalancerPoliciesOfListenerRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new SetLoadBalancerPoliciesOfListenerRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public SetLoadBalancerPoliciesOfListenerRequest unmarshall(
			final Map<String, String[]> in) throws Exception {
		final SetLoadBalancerPoliciesOfListenerRequest req = new SetLoadBalancerPoliciesOfListenerRequest();

		req.setLoadBalancerName(QueryUtil
				.requiredString(in, "LoadBalancerName"));
		req.setLoadBalancerPort(Integer.parseInt(in.get("LoadBalancerPort")[0]));

		final Collection<String> policyNames = new ArrayList<String>();
		int i = 0;
		while (true) {
			i++;
			final String[] l = in.get("PolicyNames.member." + i);
			if (l == null) {
				break;
			}
			policyNames.add(l[0]);
		}
		req.setPolicyNames(policyNames);
		return req;
	}
}
