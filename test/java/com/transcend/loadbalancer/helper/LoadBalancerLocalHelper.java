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
package com.transcend.loadbalancer.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.msi.tough.workflow.WorkflowSubmitter;

/**
 * Account helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
public class LoadBalancerLocalHelper extends AbstractLoadBalancerHelper {

    /**
     * Redefine the submitter as default (send to in-process workflow).
     */
    @Resource
    public void setWorkflowSubmitter(WorkflowSubmitter submitter) {
        this.workflowSubmitter = submitter;
    }
}
