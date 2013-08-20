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
/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.transcend.loadbalancer.helper;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.helper.AbstractHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.workflow.WorkflowSubmitter;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerRequest;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerRequest;
import com.transcend.loadbalancer.message.DescribeLoadBalancersMessage.DescribeLoadBalancersRequest;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

/**
 * @author jgardner
 *
 */
public abstract class AbstractLoadBalancerHelper extends AbstractHelper<String> {
    private final static Logger logger = Appctx
            .getLogger(AbstractLoadBalancerHelper.class.getName());

    @Resource(name = "accessKey")
    String accessKey = null;

    @Resource
    protected String defaultAvailabilityZone = null;

    /**
     *
     */
    public AbstractLoadBalancerHelper() {
        super();
    }

    public abstract void setWorkflowSubmitter(WorkflowSubmitter submitter);

    /**
     * Construct a minimal valid LB request.
     *
     * @param lbName
     * @return
     */
    public CreateLoadBalancerRequest.Builder createLoadBalancerRequest(String lbName) {
        final CreateLoadBalancerRequest.Builder request =
                CreateLoadBalancerRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setRequestId(lbName);
        request.setLoadBalancerName(lbName);
        request.addAvailabilityZone(defaultAvailabilityZone);
        Listener.Builder listener = Listener.newBuilder();
        listener.setProtocol("http");
        listener.setLoadBalancerPort(80);
        listener.setInstancePort(80);
        listener.setInstanceProtocol("http");
        request.addListener(listener);
        request.setAction("CreateLB");
        return request;
    }

    /**
     * Construct a minimal valid describe LB request.
     *
     * @param lbName
     * @return
     */
    public DescribeLoadBalancersRequest.Builder describeLoadBalancerRequest(String lbName) {
        final DescribeLoadBalancersRequest.Builder request =
                DescribeLoadBalancersRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        if (lbName != null) {
            request.addLoadBalancerNames(lbName);
            request.setRequestId(lbName);
        } else {
            String uniqueId = UUID.randomUUID().toString().substring(0, 10);
            request.setRequestId(uniqueId);
        }
        request.setAction("DescLB");
        return request;
    }

    /**
     * Create a load balancer.
     *
     * @param name
     */
    public void createLoadBalancer(String name) throws Exception {
        CreateLoadBalancerRequest.Builder request =
                createLoadBalancerRequest(name);
        submitAndWait(request.build());
        addEntity(name);
    }

    /**
     * Obtain or create a load balancer (may be shared with other tests).
     *
     * @param suggestedName name to be used, if created.
     */
    public String getOrCreateLoadBalancer(String suggestedName) throws Exception {
        Collection<String> existing = getExistingEntities();
        if (! existing.isEmpty()) {
            String name = existing.iterator().next();
            logger.debug("Found existing: " + name);
            DescribeLoadBalancersRequest.Builder request =
                    describeLoadBalancerRequest(name);
            try {
                // Describe it, to make sure it's still there.
                submitAndWait(request.build());
                return name;
            } catch (ErrorResponse e) {
                logger.debug("Existing: " + name + " not found, create new.");
            }
        }
        CreateLoadBalancerRequest.Builder request =
                createLoadBalancerRequest(suggestedName);
        submitAndWait(request.build());
        addEntity(suggestedName);
        return suggestedName;
    }

    /**
     * Construct a delete LB request.
     *
     * @param userName
     * @return
     */
    public DeleteLoadBalancerRequest deleteLoadBalancerRequest(String name) {
        DeleteLoadBalancerRequest.Builder request =
                DeleteLoadBalancerRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(accessKey);
        request.setRequestId(name);
        request.setLoadBalancerName(name);
        request.setAction("DelLB");
        return request.build();
    }

    /**
     * Delete an account with the given name.
     *
     * @param name
     */
    public void deleteLoadBalancer(String name) throws Exception {
        DeleteLoadBalancerRequest request = deleteLoadBalancerRequest(name);
        submitAndWait(request);
        removeEntity(name);
    }

    /**
     * Delete all accounts created by tests (for test-end cleanup).
     */
    public void deleteAllCreatedLoadBalancers() throws Exception {
        // defer deletion.
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#entityName()
     */
    @Override
    public String entityName() {
        return "LoadBalancer";
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#create(java.io.Serializable)
     */
    @Override
    public void create(String identifier) throws Exception {
        createLoadBalancer(identifier);
    }

    /* (non-Javadoc)
     * @see com.msi.tough.helper.AbstractHelper#delete(java.io.Serializable)
     */
    @Override
    public void delete(String identifier) throws Exception {
        deleteLoadBalancer(identifier);
    }
}