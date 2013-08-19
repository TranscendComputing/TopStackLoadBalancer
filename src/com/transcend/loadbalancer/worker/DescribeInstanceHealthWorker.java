package com.transcend.loadbalancer.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.elasticloadbalancing.LoadBalancerQueryFaults;
import com.msi.tough.utils.InstanceUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthRequest;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthResponse;
import com.transcend.loadbalancer.message.LoadBalancerMessage.Instance;
import com.transcend.loadbalancer.message.LoadBalancerMessage.InstanceState;
import com.transcend.loadbalancer.message.LoadBalancerMessage.InstanceState.InstanceStateType;
import com.yammer.metrics.core.Meter;

public class DescribeInstanceHealthWorker extends
    AbstractWorker<DescribeInstanceHealthRequest, DescribeInstanceHealthResponse> {
    private final Logger logger = Appctx.getLogger(DescribeInstanceHealthWorker.class
    .getName());

	private static Map<String, Meter> meters = initMeter(
			"ElasticLoadbalancing", "DescribeInstanceHealth");

	protected void mark(DescribeInstanceHealthResponse ret, Exception e) {
		markStandard(meters, e);
	}

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
    public DescribeInstanceHealthResponse doWork(DescribeInstanceHealthRequest req)
            throws Exception {
        logger.debug("Performing work for DescribeInstanceHealth.");
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
    protected DescribeInstanceHealthResponse doWork0(DescribeInstanceHealthRequest req,
            ServiceRequestContext context) throws Exception {
        final AccountBean account = context.getAccountBean();
        final DescribeInstanceHealthResponse.Builder ret =
                DescribeInstanceHealthResponse.newBuilder();
        Session session = getSession();
        final String name = req.getLoadBalancerName();
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                account.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }
        if (lbean.getTarget() == null) {
            throw QueryFaults.invalidAction();
        }
        final CommaObject co = new CommaObject(lbean.getInstances());
        List<String> linsts = null;
        if (req.getInstanceCount() == 0) {
            linsts = co.getList();
        } else {
            linsts = new ArrayList<String>();
            for (final Instance in : req.getInstanceList()) {
                linsts.add(in.getInstanceId());
            }
        }

        for (final String id : linsts) {
            final boolean found = co.toList().contains(id);
            if (!found) {
                throw LoadBalancerQueryFaults.invalidInstance();
            }
            final String state = LoadBalancerUtil.getHealth(session,
                    account.getId(), id, lbean.getTarget());
            final InstanceState.Builder instState = InstanceState.newBuilder();
            final String instId = InstanceUtil.endpointBasedId(session, id);
            instState.setInstanceId(instId);
            instState.setDescription("");
            instState.setReasonCode("");
            instState.setState("running".equalsIgnoreCase(state)?
                    InstanceStateType.IN_SERVICE : InstanceStateType.OUT_OF_SERVICE);
            ret.addInstanceState(instState);
            logger.debug("instance HC " + id);
        }
        logger.debug("Response " + ret);

        return ret.buildPartial();
    }
}
