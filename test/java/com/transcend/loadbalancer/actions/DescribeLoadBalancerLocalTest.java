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
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersRequest;

/**
 * Test describe load balancer locally, within unit test VM.
 *
 * @author jgardner
 *
 */
public class DescribeLoadBalancerLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(DescribeLoadBalancerLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-dsLoc-1-" + baseName;
    String bogusName = "elb-dsLoc-2-" + baseName;

    @Resource
    private final ActionTestHelper actionTestHelper = null;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    @After
    @Transactional
    public void cleanupCreated() throws Exception {
        loadBalancerLocalHelper.deleteAllCreatedLoadBalancers();
    }

    @Before
    public void createInitial() throws Exception {
        name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
        //name1 = "elb-crLoc-2-9a723bbe";
    }

    @Test
    public void testDescribe() throws Exception {
        DescribeLoadBalancersRequest.Builder request =
                loadBalancerLocalHelper.
                describeLoadBalancerRequest(name1);
        String result =
                loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
        assertTrue("Expect at least 1 lb.",
                result.indexOf("LoadBalancerDescriptions") > 0);
        assertTrue("Expect at least 1 lb.",
                result.indexOf("DNSName") > 0);
    }

    @Test(expected=ErrorResponse.class)
    public void testDescribeNonExistent() throws Exception {
        logger.debug("Looking for LB: " + bogusName);
        DescribeLoadBalancersRequest.Builder request =
                loadBalancerLocalHelper.
                describeLoadBalancerRequest(bogusName);
        loadBalancerLocalHelper.submitAndWait(request.build());
        logger.debug("Didn't expect to find LB by bad name: " + bogusName);
    }
}
