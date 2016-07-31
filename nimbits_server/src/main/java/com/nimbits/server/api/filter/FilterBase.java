/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api.filter;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.user.User;
import com.nimbits.server.auth.AuthService;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilterBase implements Filter {


    @Autowired
    protected SettingsService settingsService;

    @Autowired
    private UserService userService;


    @Autowired
    private EntityService entityService;

    @Autowired
    private ValueService valueService;



    private Logger logger = Logger.getLogger(FilterBase.class.getName());

    private User user;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        boolean isValid = isValid(req);
        if (isValid) {

            chain.doFilter(req, resp);

        } else {

            ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must supply a " + Parameters.token.getText() + " " +
                    " parameter that matches what has been configured on this server, a user token parameter, or a " +
                    Parameters.token.getText() + " parameter in either the header or querystring that has been returned " +
                    "by a post to the Session API");

        }
    }


    @Override
    public void destroy() {

    }


    @Override
    public void init(FilterConfig config) throws ServletException {

        ServletContext context = config.getServletContext();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(context);
    }


    private boolean isValid(ServletRequest request) {
        String path = ((HttpServletRequest) request).getRequestURI();

        if (path != null && ((HttpServletRequest) request).getMethod().equals("POST") && path.startsWith("/service/v2/session")) {
            return true; //don't authenticate session posts.
        } else if (path != null && ((HttpServletRequest) request).getMethod().equals("GET") && path.startsWith("/service/v2/session")) {
            return true; //don't authenticate session gets.
        } else if (path != null && ((HttpServletRequest) request).getMethod().equals("GET") && path.startsWith("/service/v2/time")) {
            return true; //don't authenticate time api
        } else if (path != null && ((HttpServletRequest) request).getMethod().equals("POST") && path.startsWith("/service/v2/socket")) {
            return true; //don't authenticate relays to sockets
        } else if (request.getParameter(Parameters.forward.getText()) != null) {
            return true;
        }

        try {

            user = userService.getHttpRequestUser(entityService,valueService,  (HttpServletRequest) request);
            request.setAttribute(Parameters.user.getText(), user);
            return user != null;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(ex), ex);
            return false;
        }


    }


}
