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

package com.nimbits.server.auth;

import com.google.appengine.api.users.UserServiceFactory;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.settings.SettingsServiceImpl;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.dao.UserDaoImpl;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class AuthServiceImpl implements AuthService {


    private UserDao userDao;


    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;

    }

    private String getParameter(final HttpServletRequest req, Parameters parameter) {

        Map map = req.getParameterMap();
        String item = String.valueOf(map.get(parameter.name()));
        return item;

    }

    public List<EmailAddress> getCurrentUser(EntityService entityService, UserService userService, ValueService valueService, HttpServletRequest request) {
        com.google.appengine.api.users.UserService googleUserService;

        googleUserService = UserServiceFactory.getUserService();
        List<EmailAddress> result = new ArrayList<>(1);

        EmailAddress emailAddress;

        if (request != null) {
            String authToken = request.getHeader(Parameters.token.getText());
            String email = getParameter(request, Parameters.email);

            if (authToken == null) {
                authToken = request.getParameter(Parameters.token.getText());
            }
            if (authToken != null) {
                try {
                    User user = userDao.getUserByAuthToken(authToken);
                    if (user != null) {
                        return Arrays.asList(user.getEmail());
                    }
                } catch (SecurityException se) { //try to see if this is a one time thing
                    User user = userService.doLogin(entityService, valueService, request, email, authToken);
                    if (user != null) {
                        return Arrays.asList(user.getEmail());
                    }
                }

            }
        }

        if (request != null && request.getSession() != null) {
            String email = (String) request.getSession().getAttribute(Const.LOGGED_IN_EMAIL);
            if (email != null) {
                emailAddress = CommonFactory.createEmailAddress(email);
                result.add(emailAddress);
            }
        }

        if (result.isEmpty() && googleUserService != null) {
            googleUserService = UserServiceFactory.getUserService();
            com.google.appengine.api.users.User appUser = googleUserService.getCurrentUser();
            if (appUser != null) {
                String email = googleUserService.getCurrentUser().getEmail();
                if (!StringUtils.isEmpty(email)) {
                    result.add(CommonFactory.createEmailAddress(email));
                }

            }

        }

        return result;
    }

    @Override
    public boolean isGAE() {
        return true;
    }

    @Override
    public boolean isGAEAdmin() {
        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();
        final com.google.appengine.api.users.User googleUser = gaeUserService.getCurrentUser();
        if (googleUser != null) {
            return gaeUserService.isUserAdmin();

        } else {
            return false;
        }
    }

    @Override
    public String createLoginURL(String requestUri) {
        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();

        return gaeUserService.createLoginURL(requestUri);
    }

    @Override
    public String createLogoutURL(String requestUri) {
        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();

        return gaeUserService.createLogoutURL(requestUri);
    }

    @Override
    public Transport getMailTransport() {
        return null;
    }


}
