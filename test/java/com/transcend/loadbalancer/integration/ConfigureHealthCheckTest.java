package com.transcend.loadbalancer.integration;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.transcend.loadbalancer.actions.ConfigureHealthCheckLocalTest;
import com.transcend.loadbalancer.helper.LoadBalancerHelper;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckRequest;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckResponse;

public class ConfigureHealthCheckTest extends ConfigureHealthCheckLocalTest {

    private final static Logger logger = Appctx
            .getLogger(ConfigureHealthCheckTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-hc-1-" + baseName;
    String name2 = "elb-hc-2-" + baseName;

    @Resource
    LoadBalancerHelper loadBalancerHelper = null;

    @Before
    @Override
    public void createInitial() throws Exception {
        name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
        logger.debug("Using LB for testing: " + name1);
    }

    @Test(expected=Exception.class) // Exception is actually from protobuf.
    @Override
    //@Ignore
    public void testConfigureHealthCheckMissingArgs() throws Exception {
        ConfigureHealthCheckRequest.Builder request =
                configureHealthCheckRequest(name1);
        // No load balancer name
        request.clearLoadBalancerName();
        loadBalancerHelper.submitAndWait(request.build());
    }

    @Test
    @Override
    public void testGoodConfigureHealthCheck() throws Exception {
        ConfigureHealthCheckRequest.Builder request =
                configureHealthCheckRequest(name1);

        ConfigureHealthCheckResponse result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
