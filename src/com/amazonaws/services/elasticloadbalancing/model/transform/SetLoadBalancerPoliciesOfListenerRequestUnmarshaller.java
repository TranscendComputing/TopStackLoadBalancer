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
