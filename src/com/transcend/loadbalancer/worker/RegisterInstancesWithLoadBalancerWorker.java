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

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.InstanceUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerRequest;
import com.transcend.loadbalancer.message.RegisterInstancesWithLoadBalancerMessage.RegisterInstancesWithLoadBalancerResponse;

public class RegisterInstancesWithLoadBalancerWorker
        extends
        AbstractWorker<RegisterInstancesWithLoadBalancerRequest, RegisterInstancesWithLoadBalancerResponse> {

    private final Logger logger = Appctx
            .getLogger(RegisterInstancesWithLoadBalancerWorker.class.getName());

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
    public RegisterInstancesWithLoadBalancerResponse doWork(
            RegisterInstancesWithLoadBalancerRequest req) throws Exception {
        logger.debug("Performing work for RegisterInstancesWithLoadBalancer.");
        return super.doWork(req, getSession());
    }

    @Override
    protected RegisterInstancesWithLoadBalancerResponse doWork0(
            RegisterInstancesWithLoadBalancerRequest r,
            ServiceRequestContext context) throws Exception {
        final RegisterInstancesWithLoadBalancerResponse.Builder ret =
                RegisterInstancesWithLoadBalancerResponse.newBuilder();

        final String name = r.getLoadBalancerName();
        logger.debug("Operation RegisterInstancesWithLoadBalancer " + name);

        // find out if load balancer exists
        final Session session = getSession();
        final AccountBean ac = context.getAccountBean();
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                ac.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }

        final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
                session, lbean);

        final List<String> instances = lbtype.getInstances();
        for (final String in : r.getInstanceList()) {
            String instId = null;

            final InstanceBean inst = InstanceUtil.getInstance(session, in);
            if (inst == null) {
                final String dns = InstanceUtil.getInstanceDns(session,
                        AccountUtil.toAccount(ac), in);
                if (dns == null) {
                    return null;
                }
                final InstanceBean ib = new InstanceBean();
                ib.setUserId(ac.getId());
                ib.setInstanceId(in);
                ib.setPublicIpId(dns);
                session.save(ib);
                instId = ib.getInstanceId();
            } else {
                instId = in;
            }

            if (instId == null) {
                throw LoadBalancerQueryFaults.invalidInstance();
            }

            if (!instances.contains(in)) {
                instances.add(instId);
            }
        }

        for (String inst : instances) {
            ret.addInstance(inst);
        }
        ret.setLoadBalancerName(name);
        RegisterInstancesWithLoadBalancerResponse result = ret.buildPartial();
        logger.debug("Response " + result.toString());
        return result;
    }

}
