package com.msi.tough.query.serviceloadbalancer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.msi.tough.core.Appctx;

public class ServiceLoadBalancerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) {
		try {
			final ServiceLoadBalancerImpl impl = Appctx
					.getBean("ServiceLoadBalancerImpl");
			impl.process(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
