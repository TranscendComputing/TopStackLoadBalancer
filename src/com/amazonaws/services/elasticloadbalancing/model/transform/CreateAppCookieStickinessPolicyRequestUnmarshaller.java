package com.amazonaws.services.elasticloadbalancing.model.transform;

import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.CreateAppCookieStickinessPolicyMessage.CreateAppCookieStickinessPolicyRequest;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class CreateAppCookieStickinessPolicyRequestUnmarshaller
        implements
        Unmarshaller<CreateAppCookieStickinessPolicyRequest, Map<String, String[]>> {

    private static CreateAppCookieStickinessPolicyRequestUnmarshaller instance;

    public static CreateAppCookieStickinessPolicyRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateAppCookieStickinessPolicyRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public CreateAppCookieStickinessPolicyRequest unmarshall(
            final Map<String, String[]> in) {
        final CreateAppCookieStickinessPolicyRequest.Builder req =
                CreateAppCookieStickinessPolicyRequest
                .newBuilder();
        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));
        req.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
        req.setCookieName(QueryUtil.requiredString(in, "CookieName"));
        return req.buildPartial();
    }
}
