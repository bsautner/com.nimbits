/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.spring;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
public class SpringGwtRemoteServiceServlet extends RemoteServiceServlet {


    @Override
    public void init() {

    }

    @Override
    public String processCall(String payload) throws SerializationException {
        try {
            Object handler = getBean(getThreadLocalRequest());
            RPCRequest rpcRequest = RPC.decodeRequest(payload, handler.getClass(), this);
            onAfterRequestDeserialized(rpcRequest);

            return RPC.invokeAndEncodeResponse(handler, rpcRequest.getMethod(), rpcRequest.getParameters(), rpcRequest
                    .getSerializationPolicy());
        } catch (IncompatibleRemoteServiceException ex) {

            return RPC.encodeResponseForFailure(null, ex);
        }
    }

    /**
     * Determine Spring bean to handle request based on request URL, e.g. a
     * request ending in /myService will be handled by bean with name
     * "myService".
     *
     * @param request
     * @return handler bean
     */
    protected Object getBean(final HttpServletRequest request) {
        final String service = getService(request);
        final Object bean = getBean(service);
        if (!(bean instanceof RemoteService)) {
            throw new IllegalArgumentException("Spring bean is not a GWT RemoteService: " + service + " (" + bean + ")");
        }

        return bean;
    }

    /**
     * Parse the service name from the request URL.
     *
     * @param request
     * @return bean name
     */
    protected String getService(final HttpServletRequest request) {
        final String url = request.getRequestURI();

        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * Look up a spring bean with the specified name in the current web
     * application context.
     *
     * @param name
     *            bean name
     * @return the bean
     */
    protected Object getBean(String name) {


        final WebApplicationContext applicationContext = WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
        if (applicationContext == null) {
            throw new IllegalStateException("No Spring web application context found");
        }
        if (!applicationContext.containsBean(name)) {
            {
                throw new IllegalArgumentException("Spring bean not found: " + name);
            }
        }
        return applicationContext.getBean(name);
    }
}
