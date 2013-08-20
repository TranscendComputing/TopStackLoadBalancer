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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.elasticloadbalancing.ListenerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.StringHelper;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ListenerBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerRequest;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerResult;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Listener;

public class CreateLoadBalancerWorker extends
        AbstractWorker<CreateLoadBalancerRequest, CreateLoadBalancerResult> {
    private final Logger logger = Appctx.getLogger(CreateLoadBalancerWorker.class
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
    public CreateLoadBalancerResult doWork(CreateLoadBalancerRequest req)
            throws Exception {
        logger.debug("Performing work for CreateLoadBalancer.");
        return super.doWork(req, getSession());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.workflow.core.AbstractWorker#doWork0(com.google.protobuf
     * .Message, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    protected CreateLoadBalancerResult doWork0(CreateLoadBalancerRequest req,
            ServiceRequestContext context) throws Exception {
        final AccountBean account = context.getAccountBean();
        final CreateLoadBalancerResult.Builder ret =
                CreateLoadBalancerResult.newBuilder();

        final String name = req.getLoadBalancerName();
        logger.debug("Operation createLoadBalancer " + name);
        if (!StringHelper.validateName(name)) {
            throw QueryFaults.InvalidParameterValue();
        }

        Session session = getSession();
        // find out if load balancer is already created
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean != null) {
            throw LoadBalancerQueryFaults.duplicateLoadBalancer();
        }
        create(session, account, req, ret);
        ret.setDNSName("0.0.0.0");
        logger.debug("Response " + ret.toString());
        return ret.buildPartial();
    }

    /**
    * Create LoadBalancer
    *
    * @param session
    * @param ac
    * @param req
    * @return
    * @throws Exception
    */
   private void create(final Session session,
           final AccountBean ac, final CreateLoadBalancerRequest req,
           CreateLoadBalancerResult.Builder ret)
           throws Exception {
       final String name = req.getLoadBalancerName();
       List<String> avzs = req.getAvailabilityZoneList();
       if (avzs == null || avzs.size() == 0) {
           avzs = Arrays.asList(new String[] { ac.getDefZone() });
       }

       // save LB basic data
       final LoadBalancerBean lb = new LoadBalancerBean();
       lb.setUserId(ac.getId());
       lb.setLoadBalancerName(name);
       lb.setCreatedTime(new Date());
       lb.setLbStatus("creating");
       lb.setDnsName("0.0.0.0");
       lb.setInconfig(true);
       lb.setReconfig(false);
       lb.setStackId("__elb_" + StringHelper.randomStringFromTime());
       lb.setSgName(LoadBalancerUtil.getSecGrpName(ac.getId(), name));
       lb.setAvzones(avzs.get(0));
       session.save(lb);

       // create listeners
       final Set<ListenerBean> ls = new HashSet<ListenerBean>();
       for (final Listener lsn : req.getListenerList()) {
           final ListenerType ltype = new ListenerType();
           ltype.setInstancePort(Integer.toString(lsn.getInstancePort()));
           ltype.setLoadBalancerPort(Integer.toString(lsn.getLoadBalancerPort()));
           final String protocol = lsn.getProtocol().toLowerCase();
           if (!protocol.equals("http")) {
               throw LoadBalancerQueryFaults.invalidConfigurationRequest();
           }
           ltype.setProtocol(protocol);
           String instanceProtocol = lsn.hasProtocol()?
                   lsn.getInstanceProtocol() : null;
           if (instanceProtocol == null) {
               instanceProtocol = protocol;
           }
           instanceProtocol = instanceProtocol.toLowerCase();
           if (!instanceProtocol.equals("http")) {
               throw LoadBalancerQueryFaults.invalidConfigurationRequest();
           }
           ltype.setSSLCertificateId(lsn.getSSLCertificateId());

           final ListenerBean lsnb = new ListenerBean();
           lsnb.setProtocol(ltype.getProtocol());
           lsnb.setInstancePort(Long.parseLong(ltype.getInstancePort()));
           lsnb.setLoadBalancerPort(Long.parseLong(ltype.getLoadBalancerPort()));
           session.save(lsnb);
           ls.add(lsnb);
       }
       lb.setListeners(ls);
       session.save(lb);

       ret.setLoadBalancerName(name);
       ret.addAllListeners(req.getListenerList());
       ret.addAllAvailabilityZone(avzs);
       ret.setStackId(lb.getStackId());
   }
}