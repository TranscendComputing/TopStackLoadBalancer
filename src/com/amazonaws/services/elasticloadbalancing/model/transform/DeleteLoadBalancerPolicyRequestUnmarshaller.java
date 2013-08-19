package com.amazonaws.services.elasticloadbalancing.model.transform;

import java.util.Map;

import com.transcend.loadbalancer.message.DeleteLoadBalancerPolicyMessage.DeleteLoadBalancerPolicyRequest;
import com.amazonaws.transform.Unmarshaller;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DeleteLoadBalancerPolicyRequestUnmarshaller implements
		Unmarshaller<DeleteLoadBalancerPolicyRequest, Map<String, String[]>> {
	// private static Logger logger = Appctx
	// .getLogger(CreateLoadBalancerListenersRequestUnmarshaller.class
	// .getName());

	private static DeleteLoadBalancerPolicyRequestUnmarshaller instance;

	public static DeleteLoadBalancerPolicyRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteLoadBalancerPolicyRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DeleteLoadBalancerPolicyRequest unmarshall(
			final Map<String, String[]> in) {
		final DeleteLoadBalancerPolicyRequest.Builder req = DeleteLoadBalancerPolicyRequest
            .newBuilder();

		req.setLoadBalancerName(in.get("LoadBalancerName")[0]);
		req.setPolicyName(in.get("PolicyName")[0]);
		return req.buildPartial();
	}
}
