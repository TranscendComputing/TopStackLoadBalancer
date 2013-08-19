package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.msi.tough.query.AbstractAction;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryFaults;
import com.yammer.metrics.core.Meter;

public class DetachLoadBalancerFromSubnets extends AbstractAction<Object> {

	private static Map<String, Meter> meters = initMeter(
			"ElasticLoadbalancing", "DetachLoadBalancerFromSubnets");

	@Override
	protected void mark(Object ret, Exception e) {
		markStandard(meters, e);
	}

	@Override
	public String marshall(final MarshallStruct<Object> input,
			final HttpServletResponse resp) throws Exception {
		return null;
	}

	@Override
	public Object process0(final Session session, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		throw QueryFaults.notSupported();
	}
}
