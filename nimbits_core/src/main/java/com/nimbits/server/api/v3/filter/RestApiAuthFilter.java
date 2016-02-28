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

package com.nimbits.server.api.v3.filter;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.filter.ClientRequest;
import com.nimbits.server.api.filter.FilterBase;
import com.nimbits.server.auth.AuthService;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.dao.UserDao;
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

public class RestApiAuthFilter implements Filter {


    @Autowired
    protected SettingsService settingsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private NimbitsCache nimbitsCache;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ValueService valueService;

    @Autowired
    private UserDao userDao;

    private Logger logger = Logger.getLogger(FilterBase.class.getName());

    private User user;


    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        String ip = req.getRemoteAddr();
        String key = ip +"_" + ((HttpServletRequest) req).getRequestURI();

        if (authService.isGAE()) {
            Object o = nimbitsCache.get(key);
            int count = 0;
            if (o != null) {
                ClientRequest clientRequest = (ClientRequest) o;
                count = clientRequest.getCounter();
                if (!clientRequest.isOk()) {
                    ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "You exceeded a rate limit. Please don't hit an API at a frequency of more than " +
                            "once a minute. You can register a private cloud without a rate limit, or use a local nimbits instance to buffer your data");
                    logger.severe("Enforced a rate limit on :" + ip + "  counter: " + clientRequest.getCounter());

                    return;

                }
            }


            ClientRequest update = new ClientRequest(System.currentTimeMillis(), count + 1);
            nimbitsCache.put(key, update);
        }

        boolean isValid = isValid(req);
        if (isValid) {

            chain.doFilter(req, resp);

        } else {

            ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please add a header to your request providing basic authentication." +
                    " Authorization, Basic email:token  a token can be your password or a access key see more here: http://nimbits.com/howto_security.jsp");

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

        try {

            if (path != null && ((HttpServletRequest) request).getMethod().equals("POST") && path.endsWith("/service/v3/rest")
                    && ! userDao.usersExist()) {
                return true; //don't authenticate first time posts to the root of the API
            }

            user = userService.getHttpRequestUser(entityService, valueService, (HttpServletRequest) request);
            request.setAttribute(Parameters.user.getText(), user);
            return user != null;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(ex), ex);
            return false;
        }


    }
}
