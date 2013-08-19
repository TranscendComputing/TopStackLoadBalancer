package com.transcend.loadbalancer.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.message.CoreMessage.ErrorResult;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerHelper;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersRequest;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersResult;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.LoadBalancerDescription;

//@Ignore("Disable for now; leaking ZMQ sockets.")
public class DescribeLoadBalancersTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(DescribeLoadBalancersTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-ds-1-" + baseName;

    @Resource
    LoadBalancerHelper loadBalancerHelper = null;

    @Before
    public void setUp() throws Exception {
        logger.debug("Have a loadBalancerHelper: " + loadBalancerHelper);
        //name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
    }

    @After
    public void cleanupCreated() throws Exception {
        //loadBalancerHelper.deleteAllCreatedLoadBalancers();
    }

    /**
     * Test listing all load balancers; verify if any are present, the response
     * is fully formed.
     *
     * @throws Exception
     */
    @Test
    public void testDescribeLoadBalancers() throws Exception {
        DescribeLoadBalancersRequest.Builder request =
                loadBalancerHelper.describeLoadBalancerRequest(null);
        DescribeLoadBalancersResult result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        for (LoadBalancerDescription lbDesc :
            result.getLoadBalancerDescriptionsList()) {
            assertTrue("Expect lb name.", lbDesc.getDnsName().length() > 0);
        }
    }

    /**
     * Test that an error is returned when a load balancer is described which
     * doesn't exist.
     *
     * @throws Exception
     */
    @Test
    public void testDescribeBogusLoadBalancer() throws Exception {
        String name = "not-a-real-lb";
        logger.debug("Looking for LB: " + name);
        DescribeLoadBalancersRequest.Builder request =
                loadBalancerHelper.describeLoadBalancerRequest(name);
        ErrorResult result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
    }

    @Test
    @Ignore
    public void testDescribeKnownLoadBalancer() throws Exception {
        //name1 = "elb-crLoc-2-9a723bbe";
        logger.debug("Looking for LB: " + name1);
        DescribeLoadBalancersRequest.Builder request =
                loadBalancerHelper.describeLoadBalancerRequest(name1);
        DescribeLoadBalancersResult result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        assertTrue("Expect at least 1 lb.",
                result.getLoadBalancerDescriptionsCount() > 0);
        for (LoadBalancerDescription lbDesc :
            result.getLoadBalancerDescriptionsList()) {
            assertTrue("Expect lb name.", lbDesc.getDnsName().length() > 0);
        }
    }
}
