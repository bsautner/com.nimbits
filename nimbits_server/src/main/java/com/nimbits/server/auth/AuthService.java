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

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.dao.UserDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Deprecated
@Component//nothing but trouble
public class AuthService {


    private final SettingsService settingsService;


    private final UserDao userDao;

    @Autowired
    public AuthService(SettingsService settingsService, UserDaoImpl userDao) {
        this.settingsService = settingsService;
        this.userDao = userDao;
    }

    public List<EmailAddress> getCurrentUser(HttpServletRequest request) {

        List<EmailAddress> result = new ArrayList<>(1);

        EmailAddress emailAddress;

        String authToken = request.getHeader("token");
        if (authToken == null) {
            authToken = request.getParameter("token");
        }
        if (authToken != null) {

            User user = userDao.getUserByAuthToken(authToken);
            if (user != null) {
                return Collections.singletonList(user.getEmail());
            }

        }

        if (request.getSession() != null) {
            String email = (String) request.getSession().getAttribute(Const.LOGGED_IN_EMAIL);
            if (email != null) {
                emailAddress = CommonFactory.createEmailAddress(email);
                result.add(emailAddress);
            }
        }


        return result;
    }


    public String createLoginURL(String requestUri) {
        return Const.WEBSITE;
    }


    public String createLogoutURL(String requestUri) {
        return Const.WEBSITE;
    }


}
