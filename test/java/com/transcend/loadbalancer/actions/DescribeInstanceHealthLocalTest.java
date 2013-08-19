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
import com.msi.tough.query.ErrorResponse;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.ConfigureHealthCheckMessage.ConfigureHealthCheckRequest;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthRequest;

/**
 * Test describe load balancer locally, within unit test VM.
 *
 * @author jgardner
 *
 */
public class DescribeInstanceHealthLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(DescribeInstanceHealthLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-dsIHLoc-1-" + baseName;
    protected String bogusName = "elb-dsIHLoc-2-" + baseName;

    @Resource(name = "accessKey")
    String accessKey = null;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    ConfigureHealthCheckLocalTest configureHealthCheckTest = null;

    private static int reqCounter = 0;

    @Before
    public void createInitial() throws Exception {
        name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
        // Ensure there's a health check.
        configureHealthCheckTest = new ConfigureHealthCheckLocalTest();
        configureHealthCheckTest.accessKey = accessKey;
        ConfigureHealthCheckRequest.Builder request =
                configureHealthCheckTest.configureHealthCheckRequest(name1);
        loadBalancerLocalHelper.submitAndWait(request.build());

    }

    /**
     * Construct a minimal valid configure health check request.
     *
     * @param lbName
     * @return
     */
    public DescribeInstanceHealthRequest.Builder makeRequest(String lbName) {
        final DescribeInstanceHealthRequest.Builder request =
                DescribeInstanceHealthRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName + "-" + reqCounter++);
        request.setAction("DIHlth");
        // This test is cheating a bit, currently; there's no instance in the
        // request, so the result is trivial.
        return request;
    }

    @Test
    public void testDescribeInstanceHealth() throws Exception {
        DescribeInstanceHealthRequest.Builder request = makeRequest(name1);
        String result =
                loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }

    @Test(expected=ErrorResponse.class)
    public void testDescribeNonExistent() throws Exception {
        logger.debug("Looking for LB: " + bogusName);
        DescribeInstanceHealthRequest.Builder request = makeRequest(bogusName);
        loadBalancerLocalHelper.submitAndWait(request.build());
        logger.debug("Didn't expect to find LB by bad name: " + bogusName);
    }
}
