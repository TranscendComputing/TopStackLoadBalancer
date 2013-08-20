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
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.transcend.loadbalancer.message.CreateLBCookieStickinessPolicyMessage.CreateLBCookieStickinessPolicyRequest;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class CreateLBCookieStickinessPolicyRequestUnmarshaller
        implements
        Unmarshaller<CreateLBCookieStickinessPolicyRequest, Map<String, String[]>> {

    private static CreateLBCookieStickinessPolicyRequestUnmarshaller instance;

    public static CreateLBCookieStickinessPolicyRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateLBCookieStickinessPolicyRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public CreateLBCookieStickinessPolicyRequest unmarshall(
            final Map<String, String[]> in) {
        try {
            final CreateLBCookieStickinessPolicyRequest.Builder req =
                    CreateLBCookieStickinessPolicyRequest.newBuilder();
            req.setLoadBalancerName(QueryUtil.requiredString(in,
                    "LoadBalancerName"));
            req.setPolicyName(QueryUtil.requiredString(in, "PolicyName"));
            Long expirationPeriod = null;
            expirationPeriod = QueryUtil.getLong(in, "CookieExpirationPeriod");
            if (expirationPeriod != null) {
                if (expirationPeriod <= 0) {
                    throw LoadBalancerQueryFaults.validationError();
                }
                req.setCookieExpirationPeriod(expirationPeriod.intValue());
            }
            return req.buildPartial();
        } catch (final ErrorResponse err) {
            throw err;
        } catch (final Exception e) {
            throw LoadBalancerQueryFaults.validationError();
        }
    }
}
