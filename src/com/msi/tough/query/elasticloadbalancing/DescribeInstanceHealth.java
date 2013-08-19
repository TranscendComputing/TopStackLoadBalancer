package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import com.amazonaws.services.elasticloadbalancing.model.transform.DescribeInstanceHealthRequestUnmarshaller;
import com.amazonaws.services.elasticloadbalancing.model.transform.DescribeInstanceHealthResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthRequest;
import com.transcend.loadbalancer.message.DescribeInstanceHealthMessage.DescribeInstanceHealthResponse;
import com.yammer.metrics.core.Meter;

public class DescribeInstanceHealth
        extends
        AbstractQueuedAction<DescribeInstanceHealthRequest, DescribeInstanceHealthResponse> {

    private static Map<String, Meter> meters = initMeter(
            "ElasticLoadbalancing", "DescribeInstanceHealth");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DescribeInstanceHealthRequest handleRequest(ServiceRequest req,
            ServiceRequestContext context) throws ErrorResponse {
        final DescribeInstanceHealthRequest r = DescribeInstanceHealthRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());
        return r;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.
     * query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
            DescribeInstanceHealthResponse message) {
        resp.setPayload(new DescribeInstanceHealthResultMarshaller()
                .marshall(message));
        return resp;
    }

    /*
    public DescribeInstanceHealthResult process0(final Session session,
            final HttpServletRequest req, final HttpServletResponse resp,
            final Map<String, String[]> map) throws Exception {
        final DescribeInstanceHealthRequest request = DescribeInstanceHealthRequestUnmarshaller
                .getInstance().unmarshall(map);
        final DescribeInstanceHealthResult ret = new DescribeInstanceHealthResult();
        final String name = request.getLoadBalancerName();
        logger.debug("Operation DescribeInstanceHealthResult " + name);
        final AccountBean ac = getAccountBean();
        final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
                ac.getId(), name);
        if (lbean == null) {
            throw LoadBalancerQueryFaults.loadBalancerNotFound();
        }
        if (lbean.getTarget() == null) {
            throw QueryFaults.invalidAction();
        }
        final CommaObject co = new CommaObject(lbean.getInstances());
        final ArrayList<InstanceState> instanceStates = new ArrayList<InstanceState>();
        List<String> linsts = null;
        if (request.getInstances() == null
                || request.getInstances().size() == 0) {
            linsts = co.getList();
        } else {
            linsts = new ArrayList<String>();
            for (final Instance in : request.getInstances()) {
                linsts.add(in.getInstanceId());
            }
        }

        for (final String id : linsts) {
            final boolean found = co.toList().contains(id);
            if (!found) {
                throw LoadBalancerQueryFaults.invalidInstance();
            }
            final String state = LoadBalancerUtil.getHealth(session,
                    ac.getId(), id, lbean.getTarget());
            final InstanceState instState = new InstanceState();
            final String instId = InstanceUtil.endpointBasedId(session, id);
            instState.setInstanceId(instId);
            instState.setDescription("");
            instState.setReasonCode("");
            instState.setState(state);
            instanceStates.add(instState);
            logger.debug("instance HC " + id);
        }
        ret.setInstanceStates(instanceStates);
        logger.debug("Response " + ret);
        return ret;
    }*/
}
