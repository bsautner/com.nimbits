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

package com.nimbits.server.transaction.user.service;

import com.google.common.base.Optional;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.server.communication.mail.EmailService;
import com.nimbits.server.transaction.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceRpcImpl extends RemoteServiceServlet implements UserServiceRpc {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    @Value("${ux.user.register_user_enabled}")
    private String registerEnabled;

    @Value("${version}")
    private String version;

    @org.springframework.beans.factory.annotation.Value("${ux.refresh_rate}")
    private String refresh;

    @Override
    public void logout(String session) {
        userDao.deleteSession(session);
        if (getThreadLocalRequest().getSession() != null) {
            getThreadLocalRequest().getSession().invalidate();
        }
    }


    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public User doLogin(String email, String password, boolean rm) {

        Optional<User> userOptional =  userService.doLogin(email, password, rm);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        else {
            return null;
        }

    }

    @Override
    public User getSession(String email, String sessionId) {
        if (userService.sessionsEnabled()) {
            Optional<User> userOptional = userDao.getUserBySession(email, sessionId);
            return userOptional.isPresent() ? userOptional.get() : null;
        }
        else {
            return null;
        }
    }

    private boolean userExists(String email) {
        Optional<User> user;
        try {
            user = userDao.getUserByEmail(email);
            return user.isPresent();
        } catch (Exception ignored) {
            return false;
        }

    }

    @Override
    public User register(String email, String password, boolean rm) {
        EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
        User user;

        if (!userExists(email)) {


            user = userService.createUserRecord(emailAddress, password, UserSource.local);
            if (userService.sessionsEnabled()) {
                String session = userDao.startSession(user, rm);
                user.setSessionId(session);
            }
            return user;

        } else {
            throw new RuntimeException("A user with that email is already registered on this system");


        }

    }

    @Override
    public User resetPassword(String email, String password, String recoveryToken) {

        Optional<User> optional = userService.getUserByKey(email);

        if (optional.isPresent()) {


            User u = optional.get();
            if (u.getPasswordResetToken().equals(recoveryToken) && u.getSource().equals(UserSource.local)) {
                if (new Date().getTime() - u.getPasswordResetTimestamp() < 60000 * 5) {

                    return userService.updatePassword(u, password);
                } else {
                    throw new RuntimeException("Token has expired.");
                }
            } else {
                throw new RuntimeException("Invalid Token or not a nimbits user");
            }
        } else {
            throw new RuntimeException("User not found");
        }


    }

    @Override
    public void doForgotPassword(String value) throws Exception {
        Optional<User> optional = userService.getUserByKey(value);

        if (optional.isPresent()) {
            User user = optional.get();

            String token = UUID.randomUUID().toString() + "" + UUID.randomUUID().toString();
            userService.setResetPasswordToken(user, token);
            emailService.sendPasswordRecovery(value, token);

        }

    }

    @Override
    public Map<String, String> getSystemInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("version", version);
        map.put("registerEnabled", registerEnabled);
        map.put("refresh", refresh);
        return map;
    }

}

