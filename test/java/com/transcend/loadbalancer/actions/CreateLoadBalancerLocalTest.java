package com.transcend.loadbalancer.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ErrorResponse;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerRequest;

/**
 * Test describe load balancer locally.
 *
 * @author jgardner
 *
 */
public class CreateLoadBalancerLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(CreateLoadBalancerLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-crLoc-1-" + baseName;
    String name2 = "elb-crLoc-2-" + baseName;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    @After
    @Transactional
    public void cleanupCreated() throws Exception {
        loadBalancerLocalHelper.deleteAllCreatedLoadBalancers();
    }

    @Test
    @Ignore
    public void createOneOffLoadBalancer() throws Exception {
        final String loadBalancerName = "my-lb";
        loadBalancerLocalHelper.createLoadBalancer(loadBalancerName);
    }

    @Before
    public void createInitial() throws Exception {
        loadBalancerLocalHelper.createLoadBalancer(name1);
    }

    @Test(expected=Exception.class) // Exception is actually from protobuf.
    public void testCreateLoadBalancerMissingArgs() throws Exception {
        final CreateLoadBalancerRequest.Builder request =
                loadBalancerLocalHelper
                .createLoadBalancerRequest(name1+"-not");
        request.clearLoadBalancerName();
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test(expected = ErrorResponse.class)
    @Ignore
    public void testCreateDupBalancer() throws Exception {
        final CreateLoadBalancerRequest.Builder request =
                loadBalancerLocalHelper
                .createLoadBalancerRequest(name1);
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test
    public void testCreateGoodLoadBalancer() throws Exception {
        logger.debug("Creating balancer: "+name2);
        loadBalancerLocalHelper.createLoadBalancer(name2);
    }

}
