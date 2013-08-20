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

import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerRequest;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class CreateLoadBalancerRequestUnmarshaller implements
        Unmarshaller<CreateLoadBalancerRequest, Map<String, String[]>> {

    private static CreateLoadBalancerRequestUnmarshaller instance;

    public static CreateLoadBalancerRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateLoadBalancerRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public CreateLoadBalancerRequest unmarshall(final Map<String, String[]> in)
            throws ErrorResponse {
        final CreateLoadBalancerRequest.Builder req = CreateLoadBalancerRequest
                .newBuilder();
        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));
        final Collection<String> avzs = new ArrayList<String>();
        int i = 0;
        while (true) {
            i++;
            final String avz[] = in.get("AvailabilityZones.member." + i);
            if (avz == null) {
                break;
            }
            avzs.add(avz[0]);
        }
        req.addAllAvailabilityZone(avzs);
        i = 0;
        while (true) {
            i++;
            final String[] l = in.get("Listeners.member." + i + ".Protocol");
            if (l == null) {
                break;
            }
            final Listener.Builder lsn = Listener.newBuilder();
            lsn.setProtocol(l[0]);
            lsn.setInstancePort(Integer.parseInt(in.get("Listeners.member." + i
                    + ".InstancePort")[0]));
            lsn.setLoadBalancerPort(Integer.parseInt(in.get("Listeners.member."
                    + i + ".LoadBalancerPort")[0]));
            req.addListener(lsn);
        }
        return req.buildPartial();
    }
}
