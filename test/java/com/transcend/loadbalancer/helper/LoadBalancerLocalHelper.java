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
