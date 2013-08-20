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

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Resource;

import org.junit.Test;

import com.transcend.loadbalancer.AbstractBaseLoadBalancerTest;

/**
 * Test that unsupported actions return responses.
 *
 * @author jgardner
 *
 */
public class UnsupportedOperationTest extends AbstractBaseLoadBalancerTest {

    @Resource
    private String httpEndpoint = null;

    private HttpURLConnection openConnection(String parameters) throws Exception{
        parameters = parameters + "&AWSAccessKeyId="+getAccessKey();
        final URL url = new URL(httpEndpoint + "?" + parameters);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    @Test
    public void testApplySecurityGroupsToLoadBalancer() throws Exception {
        String parameters = "Action=ApplySecurityGroupsToLoadBalancer";
        HttpURLConnection conn = openConnection(parameters);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, conn.getResponseCode());
    }

    @Test
    public void testAttachLoadBalancerToSubnets() throws Exception {
        String parameters = "Action=AttachLoadBalancerToSubnets";
        HttpURLConnection conn = openConnection(parameters);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, conn.getResponseCode());
    }
}
