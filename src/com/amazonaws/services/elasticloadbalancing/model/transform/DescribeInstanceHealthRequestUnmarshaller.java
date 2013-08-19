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
