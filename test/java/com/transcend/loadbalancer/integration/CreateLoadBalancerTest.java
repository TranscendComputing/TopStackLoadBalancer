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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.cf.elasticloadbalancing.ListenerType;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.StringHelper;
import com.msi.tough.utils.CFUtil;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.AbstractLoadBalancerHelper;

@Ignore("Disable for now; leaking ZMQ sockets.")
public class CreateLoadBalancerTest extends AbstractBaseLoadBalancerTest {
    private final static Logger logger = Appctx
            .getLogger(CreateLoadBalancerTest.class.getName());

    protected final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS");
    private final String baseName = dateFormat.format(new Date())
            + UUID.randomUUID().toString().substring(0, 4);

    String name1 = "elb-cr-1-" + baseName;

    @Resource
    AbstractLoadBalancerHelper loadBalancerHelper = null;

    @Before
    public void setUp() throws Exception {
    }

    @Ignore
    @Test
    public void testInternalProcess() throws Exception {

        // create listeners
        final List<ListenerType> ltypes = new ArrayList<ListenerType>();
        final ListenerType ltype = new ListenerType();
        ltype.setInstancePort("80");
        ltype.setLoadBalancerPort("80");
        ltype.setProtocol("http");
        ltypes.add(ltype);

        // create load balancer type
        final LoadBalancerType lbtype = new LoadBalancerType();
        lbtype.setAvailabilityZones("nova");
        lbtype.setListeners(ltypes);

        final String script = lbtype.toCFString();
        CFUtil.runAsyncAWSScript(
                "transcend_" + StringHelper.randomStringFromTime(), 9, script,
                null);
    }

    @Test
    public void testCreateLoadBalancer() throws Exception {
        name1 = loadBalancerHelper.getOrCreateLoadBalancer(name1);
        logger.info("Got a load balancer: " + name1);
    }
}
