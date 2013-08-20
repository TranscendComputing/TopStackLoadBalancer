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
