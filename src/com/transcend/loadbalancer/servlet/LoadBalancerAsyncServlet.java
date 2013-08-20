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
/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.transcend.loadbalancer.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.AsyncServiceImpl;
import com.msi.tough.query.AsyncServiceImpl.ServiceResponseListener;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.servlet.BaseAsyncServlet;

/**
 * Servlet to receive Load Balancer requests.
 */
@WebServlet(
// servlet name
name = "loadbalancer-async", displayName = "Load Balancer request servlet",
loadOnStartup = 1,
// servlet url pattern
urlPatterns = { "/loadbalancer-async/" },
// async support needed
asyncSupported = true)
public class LoadBalancerAsyncServlet extends BaseAsyncServlet
    implements ServiceResponseListener {
    private final Logger logger = Appctx
            .getLogger(LoadBalancerAsyncServlet.class.getName());

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadBalancerAsyncServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.info("Initializing load balancer servlet.");
        super.init();
    }

    /**
     * Send the request off for processing.
     * <p/>
     * if the {@link AsyncContext#getResponse()} is null, that means this context has
     * already timed-out (and context listener has been invoked).
     */
    protected void enqueLongRunningTask(final ServiceRequest request,
            ServiceResponse response, final AsyncContext ctx) {
        try {
            final AsyncServiceImpl impl = Appctx.getBean("loadBalancerServiceAsync");
            register(impl);
            impl.process(request, response);
        } catch (final ErrorResponse errorResponse) {
            errorResponse.setRequestId(request.getRequestId());
            handleError(errorResponse);
        } catch (final Exception e) {
            e.printStackTrace();
            ErrorResponse error = ErrorResponse.InternalFailure();
            error.setRequestId(request.getRequestId());
        }
    }

}
