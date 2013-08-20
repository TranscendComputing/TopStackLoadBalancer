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
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthRequest;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Instance;

public class DescribeInstanceHealthRequestUnmarshaller implements
        Unmarshaller<DescribeInstanceHealthRequest, Map<String, String[]>> {

    private static DescribeInstanceHealthRequestUnmarshaller instance;

    public static DescribeInstanceHealthRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DescribeInstanceHealthRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DescribeInstanceHealthRequest unmarshall(
            final Map<String, String[]> in) {
        final DescribeInstanceHealthRequest.Builder req = DescribeInstanceHealthRequest
                .newBuilder();
        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));
        int i = 0;
        while (true) {
            i++;
            final String s[] = in.get("Instances.member." + i + ".InstanceId");
            if (s == null) {
                break;
            }
            final Instance.Builder ins = Instance.newBuilder();
            ins.setInstanceId(s[0]);
            req.addInstance(ins);
        }
        return req.buildPartial();
    }
}
