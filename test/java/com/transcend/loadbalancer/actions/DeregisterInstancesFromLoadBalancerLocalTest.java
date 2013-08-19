package com.transcend.loadbalancer.actions;

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
import com.msi.tough.helper.RunningInstanceHelper;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerRequest;
import com.transcend.loadbalancer.message.DeregisterInstancesFromLoadBalancerMessage.DeregisterInstancesFromLoadBalancerResponse;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerRequest;

public class DeregisterInstancesFromLoadBalancerLocalTest extends AbstractBaseLoadBalancerTest{
    private final static Logger logger = Appctx
            .getLogger(DeregisterInstancesFromLoadBalancerLocalTest.class
                    .getName());

    protected final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())
            + UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-diLoc-1-" + baseName;
    String name2 = "elb-diLoc-2-" + baseName;

    @Resource(name = "accessKey")
    String accessKey = null;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    @Resource
    RunningInstanceHelper runningInstanceHelper = null;

    RegisterInstancesWithLoadBalancerLocalTest peerTest = null;

    private boolean createdInit = false;

    @Before
    public void createInitial() throws Exception {
        createdInit = true;
    	name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
        logger.debug("######### Using LB for testing: " + name1);
    }

    public void runInstance() throws Exception {
        peerTest = new RegisterInstancesWithLoadBalancerLocalTest();
        peerTest.runningInstanceHelper = runningInstanceHelper;
        peerTest.loadBalancerLocalHelper = loadBalancerLocalHelper;
        peerTest.accessKey = accessKey;
        peerTest.name1 = name1;
        peerTest.runInstance();
    }

    @Before
    public void registerInstance() throws Exception {
        if (! createdInit) {
            createInitial();
        }
        runInstance();
        peerTest.testGoodRegisterInstance();
        Thread.sleep(2000);
    }

    /**
     * Construct a minimal valid request to deregister instances with LB.
     *
     * @param lbName
     * @return
     */
    public DeregisterInstancesFromLoadBalancerRequest.Builder deregisterInstanceRequest(
            String lbName) {
        DeregisterInstancesFromLoadBalancerRequest.Builder request =
                DeregisterInstancesFromLoadBalancerRequest
                .newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName);
        request.setAction("DeregInstLB");
        request.addInstance(peerTest.instanceId);
    	logger.debug("Request with instanceId = " + peerTest.instanceId);
        return request;
    }

    @Test
    public void testGoodDeregisterInstance() throws Exception {
        DeregisterInstancesFromLoadBalancerRequest.Builder request =
                deregisterInstanceRequest(name1);
        String result = loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
