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

import javax.annotation.Resource;

import org.junit.Test;

import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;

/**
 * Refer to CreateLoadBalancerLocalTest for complete create/delete.
 *
 */
public class DeleteLoadBalancerLocalTest extends AbstractBaseLoadBalancerTest {

    @Resource
    LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    //@Test
    /**
     * Delete an LB by name.  Uncomment test annotation to temporarily enable
     * for e.g. local cleanup.
     * @throws Exception
     */
    public void testDeleteKnownLB() throws Exception {
        String name = "lb-to-delete";
        loadBalancerLocalHelper.deleteLoadBalancer(name);
    }

    @Test
    public void testDeleteUnknownLB() throws Exception {
        String name = "not-a-real-lb";
        loadBalancerLocalHelper.deleteLoadBalancer(name);
    }
}
