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
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.actions.CreateLoadBalancerListenersLocalTest;
import com.transcend.loadbalancer.helper.AbstractLoadBalancerHelper;
import com.transcend.loadbalancer.message.CreateLoadBalancerListenersMessage.CreateLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerListenersMessage.DeleteLoadBalancerListenersRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerListenersMessage.DeleteLoadBalancerListenersResult;

public class DeleteLoadBalancerListenersTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(DeleteLoadBalancerListenersTest.class.getName());

    protected final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS");
    private final String baseName = dateFormat.format(new Date())
            + UUID.randomUUID().toString().substring(0, 3);

    String name1 = "elb-dtl-1-" + baseName;

    @Resource
    AbstractLoadBalancerHelper loadBalancerHelper = null;

    private static int reqCounter = 0;

    private CreateLoadBalancerListenersRequest.Builder createRequest = null;

    @Before
    public void setUp() throws Exception {
        name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
        CreateLoadBalancerListenersLocalTest localTest =
                new CreateLoadBalancerListenersLocalTest();
        localTest.setAccessKey(getAccessKey());
        // Create a listener we can delete; save the request to reuse the test
        // port.
        createRequest = localTest.makeRequest(name1);
        loadBalancerHelper.submitAndWait(createRequest.build());
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

    @Test
    public void testDeleteLoadBalancerListeners() throws Exception {
        final DeleteLoadBalancerListenersRequest.Builder request =
                makeRequest(name1);
        DeleteLoadBalancerListenersResult result =
                loadBalancerHelper.submitAndWait(request.build());
        assertNotNull(result);
        logger.debug("Got result:"+result);
    }
}
