package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;
import com.amazonaws.services.elasticloadbalancing.model.transform.SetLoadBalancerPoliciesOfListenerRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.cf.elasticloadbalancing.LoadBalancerType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ListenerBean;
import com.msi.tough.model.LoadBalancerBean;
import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.LoadBalancerUtil;
import com.yammer.metrics.core.Meter;

public class SetLoadBalancerPoliciesOfListener extends AbstractAction<Object> {
	private final static Logger logger = Appctx
			.getLogger(SetLoadBalancerPoliciesOfListener.class.getName());

	private static Map<String, Meter> meters = initMeter(
			"ElasticLoadbalancing", "SetLoadBalancerPoliciesOfListener");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		final XMLNode xn = new XMLNode(
				"SetLoadBalancerPoliciesOfListenerResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", input.getRequestId());
		return xn.toString();
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final SetLoadBalancerPoliciesOfListenerRequest r = SetLoadBalancerPoliciesOfListenerRequestUnmarshaller
				.getInstance().unmarshall(map);

		final String name = r.getLoadBalancerName();
		logger.debug("SetLoadBalancerPoliciesOfListener " + name);
		final Integer port = r.getLoadBalancerPort();

		// find out if load balancer exists
		final AccountBean ac = getAccountBean();
		final LoadBalancerBean lbean = LoadBalancerUtil.read(session,
				ac.getId(), name);
		if (lbean == null) {
			throw LoadBalancerQueryFaults.loadBalancerNotFound();
		}
		for (final ListenerBean lsnb : lbean.getListeners()) {
			if (lsnb.getLoadBalancerPort() == port.longValue()) {
				final CommaObject co = new CommaObject(r.getPolicyNames());
				lsnb.setPolicyNames(co.toString());
				session.save(lsnb);
			}
		}
		final LoadBalancerType lbtype = LoadBalancerUtil.toLoadBalancerType(
				session, lbean);

		final String script = LoadBalancerUtil.toJson(lbtype);
		CFUtil.updateAsyncAWSScript(lbtype.getStackId(), ac.getId(), script,
				new TemplateContext(null));

		logger.debug("Response " + "");
		return "";
	}
}
