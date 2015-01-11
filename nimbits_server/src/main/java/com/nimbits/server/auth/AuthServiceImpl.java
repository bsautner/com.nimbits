/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private UserDao userDao;

    public List<EmailAddress> getCurrentUser(HttpServletRequest request) {

        List<EmailAddress> result = new ArrayList<EmailAddress>(1);

        EmailAddress emailAddress;

        String authToken = request.getHeader(Parameters.authToken.getText());
        if (authToken == null) {
            authToken = request.getParameter(Parameters.authToken.getText());
        }
        if (authToken != null) {

            User user = userDao.getUserByAuthToken(authToken);
            if (user != null) {
                return Arrays.asList(user.getEmail());
            }

        }

        if (request.getSession() != null) {
            String email = (String) request.getSession().getAttribute(Const.LOGGED_IN_EMAIL);
            if (email != null) {
                emailAddress =  CommonFactory.createEmailAddress(email);
                result.add(emailAddress);
            }
        }




        return result;
    }

    @Override
    public boolean isGAE() {
        return false;
    }

    @Override
    public boolean isGAEAdmin() {
        return false;
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
