/*
 * TopStack (c) Copyright 2012-2013 Transcend Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.msi.tough.helper.RunningInstanceHelper;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerRequest;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerResponse;

public class RegisterInstancesWithLoadBalancerLocalTest extends
        AbstractBaseLoadBalancerTest {
    private final static Logger logger = Appctx
            .getLogger(RegisterInstancesWithLoadBalancerLocalTest.class
                    .getName());

    protected final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())
            + UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-riLoc-1-" + baseName;
    String name2 = "elb-riLoc-2-" + baseName;

    @Resource(name = "accessKey")
    String accessKey = null;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    @Resource
    RunningInstanceHelper runningInstanceHelper = null;

    protected String instanceId = null;

    @Before
    public void createInitial() throws Exception {
        name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
        logger.debug("Using LB for testing: " + name1);
    }

    @Before
    public void runInstance() throws Exception {
        instanceId = runningInstanceHelper.getOrCreateInstance(name1);
        runningInstanceHelper.waitForState(instanceId, "running");
        // instance will be cleaned up in the ZTerminalUnitTest.
    }

    /**
     * Construct a minimal valid request to register instances with LB.
     *
     * @param lbName
     * @return
     */
    public RegisterInstancesWithLoadBalancerRequest.Builder registerInstanceRequest(
            String lbName) {
        final RegisterInstancesWithLoadBalancerRequest.Builder request =
                RegisterInstancesWithLoadBalancerRequest
                .newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName);
        request.setAction("RegInstLB");
        request.addInstance(instanceId);
        return request;
    }

    @Test
    public void testGoodRegisterInstance() throws Exception {
        RegisterInstancesWithLoadBalancerRequest.Builder request =
                registerInstanceRequest(name1);
        String result =
                loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
