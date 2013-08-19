package com.msi.tough.query.serviceloadbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;

public class ServiceLBUtil {
	private static String DBNAME = "__TRANSCEND__SERVICE__DATABAG__";

	@SuppressWarnings("unchecked")
	public static void reconfigure(final Session s) throws Exception {
		String localhostname = java.net.InetAddress.getLocalHost()
				.getHostName();
		Map<String, Object> config = new HashMap<String, Object>();
		List<Map<String, Object>> inports = new ArrayList<Map<String, Object>>();
		config.put("inports", inports);
		SQLQuery q = s
				.createSQLQuery("select hostnm, inport, outport, backend from ts_inst_lb order by inport, hostnm, backend");
		List<Object[]> l = q.list();

		String inport = "";
		Map<String, Object> inportm = null;

		String hostnm = "";
		List<Map<String, Object>> hosts = null;
		Map<String, Object> hostm = null;

		String backendnm = "";
		List<Map<String, Object>> backends = null;
		Map<String, Object> backendm = null;

		for (Object[] i : l) {
			if (!inport.equals(i[1].toString())) {
				inportm = new HashMap<String, Object>();
				inports.add(inportm);
				inport = i[1].toString();
				hosts = new ArrayList<Map<String, Object>>();
				inportm.put("hosts", hosts);
				hostnm = "";
			}
			inportm.put("value", i[1].toString());

			if (!hostnm.equals(i[0].toString())) {
				hostm = new HashMap<String, Object>();
				hosts.add(hostm);
				hostnm = i[0].toString();
				backends = new ArrayList<Map<String, Object>>();
				hostm.put("backends", backends);
				backendnm = "";
			}
			hostm.put("value", i[0].toString());

			if (!backendnm.equals(i[3].toString())) {
				backendm = new HashMap<String, Object>();
				backends.add(backendm);
				backendnm = i[3].toString();
			}
			backendm.put("value", i[3].toString());
			backendm.put("outport", i[2].toString());
		}

		ChefUtil.putNodeAttribute(localhostname, DBNAME, DBNAME);
		ChefUtil.createDatabag(DBNAME);
		String json = JsonUtil.toJsonString(config);
		ChefUtil.createDatabagItem(DBNAME, "config", json);
		ChefUtil.putNodeRunlist(localhostname,
				"role[transcend_service_loadbalancer]");
		//final String keyDir = (String) ConfigurationUtil
		//		.getConfiguration(Arrays.asList(new String[] { "KEYS_DIR" }));
		CFUtil.executeCommand(null, null, "chef-client");
	}
}
