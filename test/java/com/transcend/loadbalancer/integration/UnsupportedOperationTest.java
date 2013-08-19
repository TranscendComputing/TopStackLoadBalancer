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
