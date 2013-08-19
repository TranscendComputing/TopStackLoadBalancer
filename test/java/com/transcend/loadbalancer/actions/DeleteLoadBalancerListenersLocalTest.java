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
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerListenersMessage.DeleteLoadBalancerListenersRequest;

/**
 * Test create load balancer listeners locally.
 *
 * @author jgardner
 *
 */
public class DeleteLoadBalancerListenersLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(DeleteLoadBalancerListenersLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-dtList-1-" + baseName;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    private static int reqCounter = 0;

    private CreateLoadBalancerListenersRequest.Builder createRequest = null;

    @Before
    public void createInitial() throws Exception {
        name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
        CreateLoadBalancerListenersLocalTest localTest =
                new CreateLoadBalancerListenersLocalTest();
        localTest.setAccessKey(getAccessKey());
        // Create a listener we can delete; save the request to reuse the test
        // port.
        createRequest = localTest.makeRequest(name1);
        loadBalancerLocalHelper.submitAndWait(createRequest.build());
    }

    /**
     * Construct a minimal valid delete LB listener request.
     *
     * @param lbName
     * @return
     */
    public DeleteLoadBalancerListenersRequest.Builder makeRequest(String lbName) {
        final DeleteLoadBalancerListenersRequest.Builder request =
                DeleteLoadBalancerListenersRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(getAccessKey());
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName + "-" + reqCounter++);
        request.setAction("DtLBList");
        int createdPort = createRequest.getListener(0).getLoadBalancerPort();
        request.addLoadBalancerPort(createdPort);
        return request;
    }

    @Test(expected=Exception.class) // Exception is actually from protobuf.
    public void testDeleteLoadBalancerListenersMissingArgs() throws Exception {
        final DeleteLoadBalancerListenersRequest.Builder request =
                makeRequest(name1);
        request.clearLoadBalancerName();
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test
    public void testDeleteGoodLoadBalancerListeners() throws Exception {
        final DeleteLoadBalancerListenersRequest.Builder request =
                makeRequest(name1);
        String result = loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got result:"+result);
    }
}
