package com.transcend.loadbalancer.actions;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.helper.RunningInstanceHelper;
import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;
import com.transcend.loadbalancer.helper.LoadBalancerLocalHelper;

/**
 * A final test to clean up after all other tests run.  Our helpers create load
 * balancers and leave them up, since spinning up an LB takes multiple minutes.
 *
 * This will delete LBs, even from previous runs, since a file written.
 *
 * @author jgardner
 *
 */
public class ZTerminalUnitTest extends AbstractBaseLoadBalancerTest {

    private final static Logger logger = Appctx
            .getLogger(ZTerminalUnitTest.class.getName());

    @Resource
    private LoadBalancerLocalHelper loadBalancerLocalHelper = null;

    @Resource
    RunningInstanceHelper runningInstanceHelper = null;

    private static ZTerminalUnitTest instance = null;

    @Before
    public void setUp() throws Exception {
        instance = this;
    }

    @Test
    public void noop() {

    }

    @AfterClass
    public static void cleanupCreated() throws Exception {
        logger.debug("Destroying assets: all tests complete.");
        instance.loadBalancerLocalHelper.finalDestroy();

        instance.runningInstanceHelper.finalDestroy();
    }

}
