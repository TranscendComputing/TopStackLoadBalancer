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
public class LoadBalancerHelper extends AbstractLoadBalancerHelper {

    /**
     * Redefine the submitter to go to a remote app.
     */
    @Resource(name="workflowSubmitterToWebapp")
    public void setWorkflowSubmitter(WorkflowSubmitter submitter) {
        this.workflowSubmitter = submitter;
    }


}
