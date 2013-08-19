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
