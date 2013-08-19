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
