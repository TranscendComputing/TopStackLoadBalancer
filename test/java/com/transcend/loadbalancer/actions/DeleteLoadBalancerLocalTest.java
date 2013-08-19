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
