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
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.actions.CreateLoadBalancerListenersLocalTest;
import com.transcend.loadbalancer.helper.AbstractLoadBalancerHelper;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersResult;

public class CreateLoadBalancerListenersTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(CreateLoadBalancerListenersTest.class.getName());

    protected final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS");
    private final String baseName = dateFormat.format(new Date())
            + UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-crl-1-" + baseName;

    @Resource
    AbstractLoadBalancerHelper loadBalancerHelper = null;

    @Before
    public void setUp() throws Exception {
        name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
    }

    public CreateLoadBalancerListenersRequest.Builder makeRequest(String lbName) {
        CreateLoadBalancerListenersLocalTest localTest =
                new CreateLoadBalancerListenersLocalTest();
        localTest.setAccessKey(getAccessKey());
        return localTest.makeRequest(lbName);
    }

    @Test
    public void testCreateLoadBalancerListeners() throws Exception {
        final CreateLoadBalancerListenersRequest.Builder request =
                makeRequest(name1);
        CreateLoadBalancerListenersResult result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got result:"+result);
    }
}
