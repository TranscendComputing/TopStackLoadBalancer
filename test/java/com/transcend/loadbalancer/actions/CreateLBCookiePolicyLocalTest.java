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
import com.transcend.loadbalancer.message.CreateLBCookieStickinessPolicyMessage.CreateLBCookieStickinessPolicyRequest;

public class CreateLBCookiePolicyLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(CreateLBCookiePolicyLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    private static int reqCounter = 0;

    String name1 = "elb-lbckLoc-1-" + baseName;
    String name2 = "elb-lbckLoc-2-" + baseName;

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
     * Construct a minimal valid LB cookie policy request.
     *
     * @param lbName
     * @return
     */
    public CreateLBCookieStickinessPolicyRequest.Builder makeRequest(String lbName) {
        final CreateLBCookieStickinessPolicyRequest.Builder request =
                CreateLBCookieStickinessPolicyRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName + "-" + reqCounter++);
        request.setAction("CrLBStick");
        request.setCookieExpirationPeriod(600);
        request.setPolicyName(lbName + "-" + reqCounter++);
        return request;
    }


    @Test(expected=Exception.class) // Exception is actually from protobuf.
    public void testCreateLBCookiePolicyMissingArgs() throws Exception {
        CreateLBCookieStickinessPolicyRequest.Builder request =
                makeRequest(name1);
        // No load balancer name
        request.clearLoadBalancerName();
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test
    public void testGoodCreateLBCookiePolicy() throws Exception {
        CreateLBCookieStickinessPolicyRequest.Builder request =
                makeRequest(name1);

        String result =
                loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
