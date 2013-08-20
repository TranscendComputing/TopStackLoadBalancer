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
