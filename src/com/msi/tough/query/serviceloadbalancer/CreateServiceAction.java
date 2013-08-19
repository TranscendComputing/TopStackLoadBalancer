package com.msi.tough.query.serviceloadbalancer;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.UnsecuredAction;

public class CreateServiceAction extends UnsecuredAction {

	private final Logger logger = Appctx.getLogger(CreateServiceAction.class
			.getName());

	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {

		final String hostname = QueryUtil.getString(map, "hostname");
		final String inport = QueryUtil.getString(map, "inport");
		final String outport = QueryUtil.getString(map, "outport");
		final String backend = QueryUtil.getString(map, "backend");
		if (hostname == null || inport == null || outport == null
				|| backend == null) {
			return "All paremeters not sent";
		}
		SQLQuery q = s
				.createSQLQuery("select * from ts_inst_lb  where hostnm='"
						+ hostname + "' and inport=" + inport + " and outport="
						+ outport + " and  backend='" + backend + "'");
		if (q.list().size() > 0) {
			return "Already Exists";
		}
		SQLQuery i = s
				.createSQLQuery("insert into ts_inst_lb (hostnm,inport,outport,backend) values ('"
						+ hostname
						+ "',"
						+ inport
						+ ","
						+ outport
						+ ",'"
						+ backend + "')");
		i.executeUpdate();
		ServiceLBUtil.reconfigure(s);
		return "CREATED";
	}
}
