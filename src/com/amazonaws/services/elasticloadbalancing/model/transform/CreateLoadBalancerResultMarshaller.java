package com.amazonaws.services.elasticloadbalancing.model.transform;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.CreateLoadBalancerMessage.CreateLoadBalancerResult;

/**
 * Create Load Balancer Request Marshaller
 */
public class CreateLoadBalancerResultMarshaller {

	public String marshall(CreateLoadBalancerResult in)
			throws ErrorResponse {
		final XMLNode xn = new XMLNode("CreateLoadBalancerResponse");
		xn.addAttr("xmlns",
				"http://elasticloadbalancing.amazonaws.com/doc/2010-07-01/");
		final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
		QueryUtil.addNode(mmeta, "RequestId", in.getRequestId());
		final XMLNode nr = new XMLNode("CreateLoadBalancerResult");
		xn.addNode(nr);
		final XMLNode ndns = new XMLNode("DNSName");
		nr.addNode(ndns);
		final XMLNode ndns0 = new XMLNode();
		ndns.addNode(ndns0);
		ndns0.setPlaintext(in.getDNSName());
		System.out.println("DNSName " + in.getDNSName());
		return xn.toString();
	}
}
