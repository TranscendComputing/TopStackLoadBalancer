package com.msi.tough.query.elasticloadbalancing;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.msi.tough.core.Appctx;
import com.msi.tough.utils.Constants;

public class ELBUIDServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		try {
			Appctx.setThreadMap(Constants.ENDPOINT_OPTIONS,
					Appctx.getBean("elasticLoadBalancingQuery_uuid_options"));
			final ElasticLoadBalancingImpl impl = Appctx
					.getBean("elasticLoadBalancingQuery");
			impl.process(req, resp);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			Appctx.removeThreadMap();
		}
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		try {
			Appctx.setThreadMap(Constants.ENDPOINT_OPTIONS,
					Appctx.getBean("elasticLoadBalancingQuery_uuid_options"));
			final ElasticLoadBalancingImpl impl = Appctx
					.getBean("elasticLoadBalancingQuery");
			impl.process(req, resp);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			Appctx.removeThreadMap();
		}
	}

}
