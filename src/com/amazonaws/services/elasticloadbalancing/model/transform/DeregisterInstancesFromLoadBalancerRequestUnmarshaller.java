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

import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerRequest;

/**
 * DeregisterInstancesFromLoadBalancerRequestUnmarshaller
 */
public class DeregisterInstancesFromLoadBalancerRequestUnmarshaller
        implements
        Unmarshaller<DeregisterInstancesFromLoadBalancerRequest, Map<String, String[]>> {

    private static DeregisterInstancesFromLoadBalancerRequestUnmarshaller instance;

    public static DeregisterInstancesFromLoadBalancerRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DeregisterInstancesFromLoadBalancerRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DeregisterInstancesFromLoadBalancerRequest unmarshall(
            final Map<String, String[]> in) throws Exception {
        final DeregisterInstancesFromLoadBalancerRequest.Builder
            req = DeregisterInstancesFromLoadBalancerRequest.newBuilder();
        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));
        int i = 0;
        while (true) {
            i++;
            final String s[] = in.get("Instances.member." + i + ".InstanceId");
            if (s == null) {
                break;
            }
            req.addInstance(s[0]);
        }
        return req.buildPartial();
    }
}
