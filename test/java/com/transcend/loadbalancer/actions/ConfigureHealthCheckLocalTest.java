package com.transcend.loadbalancer.actions;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckRequest;
import com.transcend.loadbalancer.message.LoadBalancerMessage.HealthCheck;

public class ConfigureHealthCheckLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(ConfigureHealthCheckLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-hcLoc-1-" + baseName;
    String name2 = "elb-hcLoc-2-" + baseName;

    @Resource(name = "accessKey")
    String accessKey = null;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    @Before
    public void createInitial() throws Exception {
        name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
        logger.debug("Using LB for testing: " + name1);
    }

    /**
     * Construct a minimal valid configure health check request.
     *
     * @param lbName
     * @return
     */
    public ConfigureHealthCheckRequest.Builder configureHealthCheckRequest(String lbName) {
        final ConfigureHealthCheckRequest.Builder request =
                ConfigureHealthCheckRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName);
        request.setAction("ConfHC");
        HealthCheck.Builder healthCheck = HealthCheck.newBuilder();
        // 120 secs between checks.
        healthCheck.setInterval(120);
        // Require 2 checks to go healthy.
        healthCheck.setHealthyThreshold(2);
        // Check HTTP to 80, /status
        healthCheck.setTarget("HTTP:80/status");
        // >30 sec is unhealthy
        healthCheck.setTimeout(30);
        // Consider 1 failed check unhealthy
        healthCheck.setUnhealthyThreshold(1);
        request.setHealthCheck(healthCheck);
        return request;
    }


    @Test(expected=Exception.class) // Exception is actually from protobuf.
    public void testConfigureHealthCheckMissingArgs() throws Exception {
        ConfigureHealthCheckRequest.Builder request =
                configureHealthCheckRequest(name1);
        // No load balancer name
        request.clearLoadBalancerName();
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test
    public void testGoodConfigureHealthCheck() throws Exception {
        ConfigureHealthCheckRequest.Builder request =
                configureHealthCheckRequest(name1);

        String result =
                loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
