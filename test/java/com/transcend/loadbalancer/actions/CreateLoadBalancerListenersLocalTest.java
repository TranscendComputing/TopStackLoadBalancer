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
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.ErrorResponse;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

/**
 * Test create load balancer listeners locally.
 *
 * @author jgardner
 *
 */
public class CreateLoadBalancerListenersLocalTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(CreateLoadBalancerListenersLocalTest.class.getName());

    protected final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
    private final String baseName = dateFormat.format(new Date())+
            UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-crList-1-" + baseName;

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    private static int reqCounter = 0;

    @Before
    public void createInitial() throws Exception {
        name1 = loadBalancerLocalHelper.getOrCreateLoadBalancer(name1);
    }

    /**
     * Construct a minimal valid create LB listener request.
     *
     * @param lbName
     * @return
     */
    public CreateLoadBalancerListenersRequest.Builder makeRequest(String lbName) {
        final CreateLoadBalancerListenersRequest.Builder request =
                CreateLoadBalancerListenersRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(getAccessKey());
        request.setLoadBalancerName(lbName);
        request.setRequestId(lbName + "-" + reqCounter++);
        request.setAction("CrLBList");
        Listener.Builder listener = Listener.newBuilder();
        listener.setProtocol("http");
        listener.setLoadBalancerPort(8080);
        listener.setInstancePort(8080);
        listener.setInstanceProtocol("http");
        request.addListener(listener);
        return request;
    }

    @Test(expected=Exception.class) // Exception is actually from protobuf.
    public void testCreateLoadBalancerListenersMissingArgs() throws Exception {
        final CreateLoadBalancerListenersRequest.Builder request =
                makeRequest(name1);
        request.clearLoadBalancerName();
        loadBalancerLocalHelper.submitAndWait(request.build());
    }

    @Test
    public void testCreateGoodLoadBalancerListeners() throws Exception {
        final CreateLoadBalancerListenersRequest.Builder request =
                makeRequest(name1);
        String result = loadBalancerLocalHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got result:"+result);
    }

    @Test(expected=ErrorResponse.class)
    public void testCreateDuplicateLoadBalancerListener() throws Exception {
        // Sending an identical duplicate should be ok.
        try {
            final CreateLoadBalancerListenersRequest.Builder request =
                    makeRequest(name1);
            loadBalancerLocalHelper.submitAndWait(request.build());
        } catch (Exception e) {
            fail("Expect identical duplicate to succeed.");
        }
        // But an identical LB port with a different instance port should fail.
        final CreateLoadBalancerListenersRequest.Builder request2 =
                makeRequest(name1);
        Listener listener1 = request2.getListener(0);
        Listener.Builder listener2 = Listener.newBuilder();
        listener2.setProtocol(listener1.getProtocol());
        listener2.setLoadBalancerPort(listener1.getLoadBalancerPort());
        listener2.setInstancePort(listener1.getInstancePort()+555);
        listener2.setInstanceProtocol(listener1.getInstanceProtocol());
        request2.setListener(0, listener2);
        loadBalancerLocalHelper.submitAndWait(request2.build());
    }
}
