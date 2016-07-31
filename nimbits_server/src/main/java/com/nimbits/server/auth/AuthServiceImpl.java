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
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.settings.SettingsServiceImpl;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.dao.UserDaoImpl;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.stereotype.Service;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Deprecated //nothing but trouble
public class AuthServiceImpl implements AuthService {


    private final SettingsService settingsService;


    private final UserDao userDao;

    public AuthServiceImpl(SettingsServiceImpl settingsService, UserDaoImpl userDao) {
        this.settingsService = settingsService;
        this.userDao = userDao;
    }

    @Override
    public List<EmailAddress> getCurrentUser(EntityService entityService, UserService userService,  ValueService valueService, HttpServletRequest request) {

        List<EmailAddress> result = new ArrayList<>(1);

        EmailAddress emailAddress;

        String authToken = request.getHeader(Parameters.token.getText());
        if (authToken == null) {
            authToken = request.getParameter(Parameters.token.getText());
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

    @Override
    public String createLoginURL(String requestUri) {
        return Const.WEBSITE;
    }

    @Override
    public String createLogoutURL(String requestUri) {
        return Const.WEBSITE;
    }

    @Override
    public Transport getMailTransport() {
        //Use Properties object to set environment properties

        String HOST = settingsService.getSetting(ServerSetting.smtp);
        String USER = settingsService.getSetting(ServerSetting.admin);
        String PASSWORD = settingsService.getSetting(ServerSetting.smtpPassword);
        String PORT = "465";


        Properties props = new Properties();

        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.user", USER);

        String AUTH = "true";
        props.put("mail.smtp.auth", AUTH);
        String STARTTLS = "true";
        props.put("mail.smtp.starttls.enable", STARTTLS);
        String DEBUG = "true";
        props.put("mail.smtp.debug", DEBUG);

        props.put("mail.smtp.socketFactory.port", PORT);
        String SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");


        //Obtain the default mail session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);


        Transport transport = null;
        try {
            transport = session.getTransport("smtps");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return transport;

    }
}
