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
import com.msi.tough.query.ErrorResponse;
import com.transcend.loadbalancer.actions.DescribeInstanceHealthLocalTest;
import com.transcend.loadbalancer.helper.LoadBalancerHelper;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthRequest;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthResponse;

public class DescribeInstanceHealthTest
    extends DescribeInstanceHealthLocalTest {

    private final static Logger logger = Appctx
            .getLogger(DescribeInstanceHealthTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-dih-1-" + baseName;
    String name2 = "elb-dih-2-" + baseName;

    @Resource
    LoadBalancerHelper loadBalancerHelper = null;

    @Before
    @Override
    public void createInitial() throws Exception {
        name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
        logger.debug("Using LB for testing: " + name1);
    }

    @Test
    @Override
    public void testDescribeInstanceHealth() throws Exception {
        DescribeInstanceHealthRequest.Builder request =
                makeRequest(name1);

        DescribeInstanceHealthResponse result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got response: " + result);
    }

    //(expected=ErrorResponse.class)
    //Checking response; needs to throw error TODO
    @Test
    @Override
    public void testDescribeNonExistent() throws Exception {
        logger.debug("Looking for LB: " + bogusName);
        DescribeInstanceHealthRequest.Builder request = makeRequest(bogusName);
        loadBalancerHelper.submitAndWait(request.build());
        logger.debug("Didn't expect to find LB by bad name: " + bogusName);
    }

}
