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
import com.transcend.loadbalancer.message.DeleteLoadBalancerListenersMessage.DeleteLoadBalancerListenersRequest;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DeleteLoadBalancerListenersRequestUnmarshaller implements
        Unmarshaller<DeleteLoadBalancerListenersRequest, Map<String, String[]>> {

    private static DeleteLoadBalancerListenersRequestUnmarshaller instance;

    public static DeleteLoadBalancerListenersRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DeleteLoadBalancerListenersRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DeleteLoadBalancerListenersRequest unmarshall(
            final Map<String, String[]> in) {
        final DeleteLoadBalancerListenersRequest.Builder req =
                DeleteLoadBalancerListenersRequest.newBuilder();

        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));
        int i = 0;
        while (true) {
            i++;
            final String[] l = in.get("LoadBalancerPorts.member." + i);
            if (l == null) {
                break;
            }
            req.addLoadBalancerPort(Integer.parseInt(l[0]));
        }
        return req.buildPartial();
    }
}
