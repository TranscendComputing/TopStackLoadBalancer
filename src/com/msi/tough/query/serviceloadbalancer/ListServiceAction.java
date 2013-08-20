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
package com.msi.tough.query.serviceloadbalancer;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.UnsecuredAction;

public class ListServiceAction extends UnsecuredAction {

	private final Logger logger = Appctx.getLogger(ListServiceAction.class
			.getName());

	@SuppressWarnings("unchecked")
	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		SQLQuery q = s
				.createSQLQuery("select hostnm, inport, outport, backend from ts_inst_lb order by inport, hostnm, outport, backend");
		List<Object[]> l = q.list();
		final XMLNode xn = new XMLNode("ListServiceResponse");
		final XMLNode servs = QueryUtil.addNode(xn, "Services");
		for (Object[] i : l) {
			final XMLNode serv = QueryUtil.addNode(servs, "Service");
			QueryUtil.addNode(serv, "hostname", i[0].toString());
			QueryUtil.addNode(serv, "inport", i[1].toString());
			QueryUtil.addNode(serv, "outport", i[2].toString());
			QueryUtil.addNode(serv, "backend", i[3].toString());
		}
		return xn.toString();
	}
}
