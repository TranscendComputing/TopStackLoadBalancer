package com.transcend.loadbalancer.integration;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.transcend.loadbalancer.actions.RegisterInstancesWithLoadBalancerLocalTest;
import com.transcend.loadbalancer.helper.LoadBalancerHelper;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerRequest;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerResponse;

public class DeregisterInstancesFromLoadBalancerTest
    extends RegisterInstancesWithLoadBalancerLocalTest {

    private final static Logger logger = Appctx
            .getLogger(DeregisterInstancesFromLoadBalancerTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-ri-1-" + baseName;
    String name2 = "elb-ri-2-" + baseName;

    @Resource
    LoadBalancerHelper loadBalancerHelper = null;

    @Before
    @Override
    public void createInitial() throws Exception {
        name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
        logger.debug("Using LB for testing: " + name1);
    }

    @Test
    @Override
    public void testGoodRegisterInstance() throws Exception {
        RegisterInstancesWithLoadBalancerRequest.Builder request =
                registerInstanceRequest(name1);

        RegisterInstancesWithLoadBalancerResponse result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
