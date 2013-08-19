package com.msi.tough.query.elasticloadbalancing;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.Action;

public class ElasticLoadBalancingImpl {
	private final static Logger logger = Appctx
			.getLogger(ElasticLoadBalancingImpl.class.getName());

	private final Map<String, Action> actionMap;

	public ElasticLoadBalancingImpl(final Map<String, Action> actionMap) {
		this.actionMap = actionMap;
	}

	public void process(final HttpServletRequest req,
			final HttpServletResponse resp) throws Exception {
		final Action a = actionMap.get(req.getParameter("Action"));
		logger.debug("calling action " + a);
		a.process(req, resp);
	}
}
