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
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.*;
import com.nimbits.server.auth.AuthService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    public static final String ERROR1 = "Could not authenticate your request.";

    public static final int LIMIT = 1000;
    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());


    private final UserDao userDao;

    private final SettingsService settingsService;

    private final AuthService authService;

    private final EntityDao entityDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, SettingsService settingsService, AuthService authService, EntityDao entityDao) {
        this.userDao = userDao;
        this.settingsService = settingsService;
        this.authService = authService;
        this.entityDao = entityDao;
    }

    @Override
    public Optional<Credentials> credentialsWithBasicAuthentication(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null) {
            return credentialsWithBasicAuthentication(authHeader);
        }

        return Optional.absent();
    }


    @Override
    public Optional<Credentials> credentialsWithBasicAuthentication(String authHeader) {

        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                if (basic.equalsIgnoreCase("Basic")) {
                    try {
                        // String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                        String credentials =  st.nextToken();

                        int p = credentials.indexOf(":");
                        if (p != -1) {
                            String login = credentials.substring(0, p).trim();
                            String password = credentials.substring(p + 1).trim();

                            return Optional.of(new Credentials(login, password));
                        } else {
                            return Optional.absent();

                        }
                    } catch (Exception e) {
                        return Optional.absent();

                    }
                }
            }
        }

        return Optional.absent();
    }

    @Override @Deprecated
    public User getHttpRequestUser(final EntityService entityService, final ValueService valueService, final HttpServletRequest req) {
        Optional<Credentials> credentials = credentialsWithBasicAuthentication(req);

        EmailAddress email = null;

        if (credentials.isPresent()) {
            email = CommonFactory.createEmailAddress(credentials.get().getLogin());

        }

        //legacy stuff

        if (email == null) {
            email = getEmailFromRequest(req);
        }

        if (email == null) {
            email = getEmailFromLoggedInUser(entityService, valueService, req);
        }





        if (email != null) {


            User user;


            Optional<User> result = entityDao.getUser(email.getValue());


            if (! result.isPresent()) {


                return createUserIfNonexistantButAuthenticated(entityService,valueService, req, email, credentials);


            } else {
                user = result.get();
                if (credentials.isPresent()) {
                    if (! validatePassword ( entityService, valueService, user, credentials.get().getPassword())) {
                        throw new SecurityException("Invalid Password");
                    }
                }

            }

            return user;
        } else {

            throw new RuntimeException("User not found - could not get an email address from your request");

        }


    }

    private User createUserIfNonexistantButAuthenticated(final EntityService entityService, final ValueService valueService, HttpServletRequest req, EmailAddress email, Optional<Credentials> credentials) {
        User user;

        String password;
        UserSource source;
        if (credentials.isPresent()) {
            password = credentials.get().getPassword();
            source = UserSource.google;
        }
        else {
            password = UUID.randomUUID().toString();
            source = UserSource.local;
        }


        user = createUserRecord( entityService, valueService, email, password, source);

        return user;


    }



    @Override
    public EmailAddress getEmailFromRequest(HttpServletRequest req) {
        if (req != null) {
            String emailParam = req.getParameter(Parameters.email.getText());
            return StringUtils.isEmpty(emailParam) ? null : CommonFactory.createEmailAddress(emailParam);
        }
        return null;
    }


    @Override
    public User createUserRecord( final EntityService entityService, final ValueService valueService, final EmailAddress internetAddress, String password, UserSource source) {
        final EntityName name = CommonFactory.createName(internetAddress.getValue(), EntityType.user);
        String passwordSalt = RandomStringUtils.randomAscii(20);
        String cryptPassword = DigestUtils.sha512Hex(password + passwordSalt);
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Attempt to create a user with a null password.");
        }
        logger.info("Creating user");
        logger.info("Creating user: " + internetAddress.getValue());
        logger.info("Creating user password: " + password);
        logger.info("Creating user passwordSalt: " + passwordSalt);
        logger.info("Creating user password: " + cryptPassword);


        boolean isFirst = ! userDao.usersExist();

        final User newUser = new UserModel.Builder()
                .name(name)
                .password(cryptPassword)
                .salt(passwordSalt)
                .source(source.name())
                .email(internetAddress.getValue())
                .owner(internetAddress.getValue())
                .parent(internetAddress.getValue())
                .create();


        newUser.setIsAdmin(isFirst);

        if (isFirst) {
            settingsService.updateSetting(ServerSetting.admin, internetAddress.getValue());
        }

        return (User) entityDao.addUpdateEntity(newUser, newUser);

    }



    @Override
    public User getAdmin() {
        final String adminStr = settingsService.getSetting(ServerSetting.admin);
        if (StringUtils.isEmpty(adminStr)) {
            throw new IllegalArgumentException("Server is missing admin setting!");
        } else {
            final User u = new UserModel.Builder()
                    .name(CommonFactory.createName(adminStr, EntityType.user))
                    .id(adminStr)
                    .email(adminStr)

                    .parent(adminStr)
                    .isAdmin(true).create();

            return u;
        }
    }

    @Override
    public Optional<User>  getUserByKey(final String key) {

        Optional<Entity> optional = entityDao.getEntity(getAdmin(), key, EntityType.user);
        if (optional.isPresent()) {
            return Optional.of((User)optional.get());
        }
        else {
            return Optional.absent();
        }


    }

    @Override
    public boolean validatePassword(final EntityService entityService, final ValueService valueService, User user, String password) {

        String storedEncodedPassword = user.getPassword();
        String salt = user.getPasswordSalt();
        String challenge = DigestUtils.sha512Hex(password + salt);

        return !StringUtils.isEmpty(password) && storedEncodedPassword.equals(challenge);


    }



    @Override
    public User doLogin(final EntityService entityService, final ValueService valueService, HttpServletRequest request, String email, String token) {

        Optional<User> optional = entityDao.getUser(email);
        if (optional.isPresent()) {
            User user = optional.get();
            if (validatePassword( entityService, valueService, user, token)) {
                return loginUser(request, email, user);
            } else {
                throw new SecurityException("Invalid user name or password");
            }
        } else {
            throw new SecurityException("User not found!");
        }
    }

    private User loginUser(HttpServletRequest request, String email, User user) {
        LoginInfo loginInfo = UserModelFactory.createLoginInfo("", "", UserStatus.newUser );
        user.setLoginInfo(loginInfo);
        String authToken = startSession(request, email);
        user.setToken(authToken);
        return user;
    }

    @Override
    public String startSession(HttpServletRequest req, String email) {


        HttpSession session = req.getSession(true);

        String authToken = UUID.randomUUID().toString();

        session.setAttribute(Const.LOGGED_IN_EMAIL, email);
        session.setAttribute(Parameters.token.getText(), authToken);
        userDao.storeAuthToken(email, authToken);
        return authToken;

    }


    @Override
    public User updatePassword(User u, String password) {

        return userDao.updatePassword(u, password);


    }

    @Override
    public void setResetPasswordToken(User user, String token) {

        userDao.setResetPasswordToken(user, token);

    }




    @Override
    public boolean userHasPoints(User user) {

        return userDao.userHasPoints(user);
    }



    protected EmailAddress getEmailFromLoggedInUser(EntityService entityService, ValueService valueService, HttpServletRequest request) {

        List<EmailAddress> emailSample = authService.getCurrentUser(entityService, this, valueService, request);
        EmailAddress emailAddress;
        if (emailSample.isEmpty()) {
            emailAddress = null;
        } else {
            emailAddress = emailSample.get(0);
        }
        if (emailAddress != null) {

            return emailAddress;

        }
        return null;
    }

    protected String getAccessKey(HttpServletRequest req) {
        String accessKey = req.getParameter(Parameters.token.getText());

        if (Utils.isEmptyString(accessKey)) {
            accessKey = req.getHeader(Parameters.token.getText());
        }


        //Legacy param names - TODO delete me
        if (Utils.isEmptyString(accessKey)) {
            accessKey = req.getParameter(Parameters.secret.getText());
        }
        if (Utils.isEmptyString(accessKey)) {
            accessKey = req.getParameter(Parameters.key.getText());
        }

        return accessKey;
    }




    protected EntityType getEntityType(HttpServletRequest req) {
        EntityType entityType = null;
        String type = req.getParameter(Parameters.type.getText());
        if (!StringUtils.isEmpty(type)) {
            Integer code = Integer.valueOf(type);
            entityType = EntityType.get(code);

        }
        if (entityType == null) {
            entityType = EntityType.point;
        }
        return entityType;
    }




}
