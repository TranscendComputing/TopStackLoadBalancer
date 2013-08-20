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
/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.elasticloadbalancing.model.transform;

import java.util.Map;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.transcend.loadbalancer.message.DeleteLoadBalancerMessage.DeleteLoadBalancerRequest;

/**
 * Describe Load Balancers Request Marshaller
 */
public class DeleteLoadBalancerRequestUnmarshaller implements
		Unmarshaller<DeleteLoadBalancerRequest, Map<String, String[]>> {

	private static DeleteLoadBalancerRequestUnmarshaller instance;

	public static DeleteLoadBalancerRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteLoadBalancerRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DeleteLoadBalancerRequest unmarshall(final Map<String, String[]> in)
			throws ErrorResponse {
		final DeleteLoadBalancerRequest.Builder req = DeleteLoadBalancerRequest.newBuilder(); 
		req.setLoadBalancerName(QueryUtil
				.requiredString(in, "LoadBalancerName"));
		return req.buildPartial();
	}
}
