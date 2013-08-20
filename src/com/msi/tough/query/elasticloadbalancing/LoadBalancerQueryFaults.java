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
package com.msi.tough.query.elasticloadbalancing;

import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;

public class LoadBalancerQueryFaults extends QueryFaults {
	public static ErrorResponse certificateNotFound() {
		return new ErrorResponse(
				"Sender",
				"The specified certificate was not found 400 Bad in the AWS Identity and Access Request Management Service.",
				"CertificateNotFound");
	}

	public static ErrorResponse duplicateListener() {
		return new ErrorResponse(
				"Sender",
				"A Listener already exists for the given LoadBalancerName and LoadBalancerPort, but with a different InstancePort, Protocol, or SSLCertificateId. ",
				"DuplicateListener");
	}

	public static ErrorResponse duplicateLoadBalancer() {
		return new ErrorResponse(
				"Sender",
				"LoadBalancer name already exists for this account. Please choose another name.",
				"DuplicateAccessPointName");
	}

	public static ErrorResponse duplicatePolicyName() {
		return new ErrorResponse("Sender",
				"Policy with the same name exists for this LoadBalancer. Please choose another name.",
				"DuplicatePolicyName");
	}

	public static ErrorResponse invalidConfigurationRequest() {
		return new ErrorResponse("Sender",
				"Requested configuration change is invalid.",
				"InvalidConfigurationRequest", 409);
	}

	public static ErrorResponse invalidInstance() {
		return new ErrorResponse("Sender",
				"The specified EndPoint is not valid.", "InvalidEndPoint");
	}

	public static ErrorResponse invalidSecurityGroup() {
		return new ErrorResponse("Sender",
				"One or more specified security groups do not exist..",
				"InvalidSecurityGroup");
	}

	public static ErrorResponse listenerNotFound() {
		return new ErrorResponse(
				"Sender",
				"Specified LoadBalancer does not have listener configured at the given port.",
				"ListenerNotFound");
	}

	public static ErrorResponse loadBalancerNotFound() {
		return new ErrorResponse("Sender",
				"The specified LoadBalancer could not be found.",
				"AccessPointNotFound");
	}

	public static ErrorResponse policyNotFound() {
		return new ErrorResponse("Sender",
				"One or more specified policies were not found.",
				"PolicyNotFound");
	}

	public static ErrorResponse tooManyLoadBalancers() {
		return new ErrorResponse(
				"Sender",
				"You have attempted to create more 400 Bad LoadBalancers than allowed.",
				"TooManyLoadBalancers");
	}

	public static ErrorResponse tooManyPolicies() {
		return new ErrorResponse("Sender",
				"Quota for number of policies has already been reached.",
				"TooManyPolicies");
	}

	public static ErrorResponse validationError() {
		return new ErrorResponse(
				"Sender",
				"A bad or out-of-range value was supplied for the input parameter.",
				"InvalidParameterValue");
	}
}
