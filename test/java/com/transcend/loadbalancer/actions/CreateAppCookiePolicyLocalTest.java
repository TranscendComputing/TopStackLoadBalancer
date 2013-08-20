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
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.CreateAppCookieStickinessPolicyMessage.CreateAppCookieStickinessPolicyRequest;

public class CreateAppCookiePolicyLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(CreateAppCookiePolicyLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    private static int reqCounter = 0;

    String name1 = "eApp-AppckLoc-1-" + baseName;
    String name2 = "eApp-AppckLoc-2-" + baseName;

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
     * Construct a minimal valid App cookie policy request.
     *
     * @param lbName
     * @return
     */
    public CreateAppCookieStickinessPolicyRequest.Builder makeRequest(String lbName) {
        final CreateAppCookieStickinessPolicyRequest.Builder request =
                CreateAppCookieStickinessPolicyRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName + "-" + reqCounter++);
        request.setAction("CrAppStick");
        request.setCookieName("somecookie");
        request.setPolicyName(baseName + reqCounter++);
        return request;
    }


    @Test(expected=Exception.class) // Exception is actually from protobuf.
    public void testCreateAppCookiePolicyMissingArgs() throws Exception {
        CreateAppCookieStickinessPolicyRequest.Builder request =
                makeRequest(name1);
        // No load balancer name
        request.clearLoadBalancerName();
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test
    public void testGoodCreateAppCookiePolicy() throws Exception {
        CreateAppCookieStickinessPolicyRequest.Builder request =
                makeRequest(name1);

        String result =
                loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }
}
