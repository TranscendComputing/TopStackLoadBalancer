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
package com.transcend.loadbalancer.worker;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractPhaseWorker;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerResponse;

public class RegisterInstancesWithLoadBalancerPhaseWorker extends AbstractPhaseWorker<RegisterInstancesWithLoadBalancerResponse>{

	private final Logger logger = Appctx.getLogger(RegisterInstancesWithLoadBalancerPhaseWorker.class
            .getName());

	/**
     * We need a local copy of this doWork to provide the transactional
     * annotation. Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public void doWork(RegisterInstancesWithLoadBalancerResponse req)
            throws Exception {
        logger.debug("Performing work for RegisterInstancesWithLoadBalancer (step 2).");
        super.doWork(req, getSession());
    }


	@Override
	protected RegisterInstancesWithLoadBalancerResponse doWork0(
			RegisterInstancesWithLoadBalancerResponse req,
			ServiceRequestContext context) throws Exception {
		final AccountBean ac = context.getAccountBean();
		final Session session = getSession();
		final String name = req.getLoadBalancerName();
		final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
				ac.getId(), name);
		final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
				session, lbean);
		lbtype.setInstances(req.getInstanceList());
		final String script = LoadBalancerUtil.toJson(lbtype);
		CFUtil.runAWSScript(session, lbtype.getStackId(), ac.getId(), script, new TemplateContext(null), true);
		return req;
	}

}
