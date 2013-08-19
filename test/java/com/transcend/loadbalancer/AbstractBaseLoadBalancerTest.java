package com.transcend.loadbalancer;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-loadBalancerContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public abstract class AbstractBaseLoadBalancerTest {

    @Resource(name = "basicAWSCredentials")
    private AWSCredentials creds;

    @Autowired
    private String defaultAvailabilityZone;

    public AWSCredentials getCreds() {
        return creds;
    }

    public void setCreds(AWSCredentials creds) {
        this.creds = creds;
    }

    public String getDefaultAvailabilityZone() {
        return defaultAvailabilityZone;
    }

    public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
        this.defaultAvailabilityZone = defaultAvailabilityZone;
    }

    /**
     * @return the accessKey
     */
    public String getAccessKey() {
        return getCreds().getAWSAccessKeyId();
    }

    /**
     * @param accessKey the accessKey to set
     */
    public void setAccessKey(String accessKey) {
        String secretKey = "N/A";
        if (getCreds() != null) {
            secretKey = getCreds().getAWSSecretKey();
        }
        this.setCreds(new BasicAWSCredentials(accessKey, secretKey));
    }
}
