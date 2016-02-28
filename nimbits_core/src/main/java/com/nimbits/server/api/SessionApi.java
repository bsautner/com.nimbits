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

package com.nimbits.server.api;

import com.google.gson.Gson;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * GET the session data for a user
 * POST Credentials if local user (not GAE) and will response with a session id
 */
public class SessionApi extends ApiBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EntityService entityService;

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        if (action != null && action.equals("logout")) {
            if (session != null) {
                userDao.deleteAuthToken(session.getId());
                session.invalidate();
            }

            resp.sendRedirect("http://www.nimbits.com");
        } else {

            initRequest(req, resp);
            User user = (User) req.getAttribute(Parameters.user.getText());

            if (user == null) {
                user = userService.getHttpRequestUser(entityService, valueService, req); //requests made with keys or apikey
            }

            if (user != null ) {
                String token = userService.getToken(req);
                user.setToken(token);

                String json =  GsonFactory.getInstance(true).toJson(user, UserModel.class);
                completeResponse(resp, json);
            } else {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "You need to post your email and password to this api first" +
                        "to start your session");
            }


        }


    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String email = req.getParameter(Parameters.email.getText());
        String token = userService.getToken(req);

        try {
            if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(token)) {
                User user = userService.doLogin(entityService, valueService, req, email, token);
                if (user.getToken() != null) {
                    Gson g =  GsonFactory.getInstance(true);
                    resp.getWriter().print(g.toJson(user));
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (SecurityException ex) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        }
    }


}
