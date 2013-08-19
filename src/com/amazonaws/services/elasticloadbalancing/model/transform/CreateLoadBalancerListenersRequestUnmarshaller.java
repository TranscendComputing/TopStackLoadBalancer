package com.amazonaws.services.elasticloadbalancing.model.transform;

import java.util.Map;

import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

/**
 * CreateLoadBalancerListenersRequestUnmarshaller
 */
public class CreateLoadBalancerListenersRequestUnmarshaller implements
        Unmarshaller<CreateLoadBalancerListenersRequest, Map<String, String[]>> {

    private static CreateLoadBalancerListenersRequestUnmarshaller instance;

    public static CreateLoadBalancerListenersRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateLoadBalancerListenersRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public CreateLoadBalancerListenersRequest unmarshall(
            final Map<String, String[]> in) {
        final CreateLoadBalancerListenersRequest.Builder req =
                CreateLoadBalancerListenersRequest.newBuilder();

        req.setLoadBalancerName(QueryUtil
                .requiredString(in, "LoadBalancerName"));

        int i = 0;
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
