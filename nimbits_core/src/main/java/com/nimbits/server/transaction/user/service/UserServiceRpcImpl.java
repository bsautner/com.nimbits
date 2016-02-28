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
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.connection.ConnectionModel;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.system.SystemDetails;
import com.nimbits.client.model.system.SystemDetailsModel;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.user.UserServiceRpc;
import com.nimbits.client.service.user.UserServiceRpcException;
import com.nimbits.server.auth.AuthService;
import com.nimbits.server.communication.mail.EmailService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UserServiceRpcImpl extends RemoteServiceServlet implements UserServiceRpc {

    private static final String ANON_NIMBITS_COM = "anon@nimbits.com";
    private static final Logger log = Logger.getLogger(UserServiceRpcImpl.class.getName());

    @Autowired
    private EntityService entityService;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ValueService valueService;

    @Override
    public User loginRpc(final String requestUri) {

        final User retObj;
        EmailAddress internetAddress = null;
        boolean isAdmin;
        boolean isFirst = !userDao.usersExist();
        UserStatus userStatus = UserStatus.unknown;

        if (isFirst) {
            userStatus = UserStatus.newServer;
        }


        isAdmin = authService.isGAEAdmin();
        List<EmailAddress> emailAddresses = authService.getCurrentUser(entityService, userService, valueService, getThreadLocalRequest());

        if (!emailAddresses.isEmpty()) {

            internetAddress = emailAddresses.get(0);
        } else {
            String sessionEmail = (String) getThreadLocalRequest().getSession().getAttribute(Const.LOGGED_IN_EMAIL);
            if (sessionEmail != null) {
                internetAddress = CommonFactory.createEmailAddress(sessionEmail);
            }
        }

        if (internetAddress != null) {

            log.info("getting user with address: " + internetAddress.getValue());

            if (! userExists(internetAddress.getValue())) {
                log.info("user not found, creating record");
                retObj = userService.createUserRecord(entityService, valueService, internetAddress, UUID.randomUUID().toString(), UserSource.google);
                userStatus = UserStatus.newUser;


            } else {
                log.info("got user result");
                Optional<User> optional = userService.getUserByKey(internetAddress.getValue());
                retObj = optional.get();


                if (userService.userHasPoints(retObj)) {
                    userStatus = UserStatus.loggedIn;

                } else {
                    userStatus = UserStatus.newUser;
                }

            }
            String authToken = userService.startSession(getThreadLocalRequest(), retObj.getEmail().getValue());

            if (isAdmin) {
                retObj.setIsAdmin(true);
            }


            LoginInfo loginInfo = UserModelFactory.createLoginInfo(authService.createLoginURL(requestUri),
                    authService.createLogoutURL(requestUri), userStatus, authService.isGAE());
            retObj.setLoginInfo(loginInfo);

            retObj.setToken(authToken);
            entityService.addUpdateEntity(valueService, retObj, retObj);


        } else {
            final EntityName name = CommonFactory.createName(ANON_NIMBITS_COM, EntityType.user);

            retObj = new UserModel.Builder().name(name).create();

            LoginInfo loginInfo = UserModelFactory.createLoginInfo(authService.createLoginURL(requestUri),
                    authService.createLogoutURL(requestUri), userStatus, authService.isGAE());
            retObj.setLoginInfo(loginInfo);


        }


        return retObj;


    }

    @Override
    public void logout() {
        if (getThreadLocalRequest().getSession() != null) {
            getThreadLocalRequest().getSession().invalidate();
        }
    }


    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public User doLogin(String email, String password) throws UserServiceRpcException {
        try {
            return userService.doLogin(entityService, valueService,  getThreadLocalRequest(), email, password);
        } catch (Exception ex) {
            throw new UserServiceRpcException(ex);
        }

    }

    private boolean userExists(String email) {
        Optional<User> user;
        try {
            user = userService.getUserByKey(email);
            if (user.isPresent()) {
                return true;
            }
            else {
                return false;
            }
        }
        catch ( Exception ignored) {
            return false;
        }

    }

    @Override
    public User register(String email, String password, String pendingConnectionToken) throws UserServiceRpcException {
        EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
        User user;


        try {
            if (! userExists(email)) {

                user = userService.createUserRecord(entityService, valueService, emailAddress, password, UserSource.local);
                LoginInfo loginInfo = UserModelFactory.createLoginInfo("", Const.WEBSITE, UserStatus.newUser, authService.isGAE());
                user.setLoginInfo(loginInfo);
                String authToken = userService.startSession(getThreadLocalRequest(), email);
                if (pendingConnectionToken != null) {
                    processConnection(email, pendingConnectionToken);
                }
                user.setToken(authToken);
                return user;

            } else {
                throw new Exception("A user with that email is already registered on this system");


            }
        } catch (Exception ex) {
            throw new UserServiceRpcException(ex);
        }

    }

    @Override
    public User resetPassword(String email, String password, String recoveryToken) throws UserServiceRpcException {

        Optional<User> optional = userService.getUserByKey(email);

        if (optional.isPresent()) {


            User u = optional.get();
            if (u.getPasswordResetToken().equals(recoveryToken) && u.getSource().equals(UserSource.local)) {
                if (new Date().getTime() - u.getPasswordResetTokenTimestamp().getTime() < 60000 * 5) {
                    User retObj = userService.updatePassword(u, password);
                    LoginInfo loginInfo = UserModelFactory.createLoginInfo("", Const.WEBSITE, UserStatus.loggedIn, authService.isGAE());
                    retObj.setLoginInfo(loginInfo);
                    String authToken = userService.startSession(getThreadLocalRequest(), email);
                    retObj.setToken(authToken);
                    return retObj;
                } else {
                    throw new UserServiceRpcException("Token has expired.");
                }
            } else {
                throw new UserServiceRpcException("Invalid Token or not a nimbits user");
            }
        }
        else {
            throw new UserServiceRpcException("User not found");
        }


    }

    @Override
    public void doForgotPassword(String value) throws UserServiceRpcException {
        Optional<User> optional = userService.getUserByKey(value);

        if (optional.isPresent()) {
            User user = optional.get();
            if (user.getSource().equals(UserSource.google)) {
                throw new UserServiceRpcException("We can't reset your password since with is account is not managed by Nimbits. Please reset your " +
                        "password using services provided by " + user.getSource().name());
            } else {
                String token = UUID.randomUUID().toString() + "" + UUID.randomUUID().toString();
                userService.setResetPasswordToken(user, token);
                emailService.sendPasswordRecovery(value, token);
            }
        }

    }

    @Override
    public Boolean processConnection(String email, String token) throws UserServiceRpcException {

        boolean success = false;
        if (token != null) {
            // User userList = userService.getUserByKey(email, AuthLevel.restricted);
            if (userExists(email)) {


                List<Connection> approveConnection = entityDao.approveConnection(token);
                if (!approveConnection.isEmpty()) {
                    Connection c = approveConnection.get(0);
                    if (c.isApproved()) {

                        emailService.sendConnectionRequestApprovalNotification(c);
                        //  Entity entity = EntityModelFactory.createEntity(c.getOwner(), "", EntityType.connection, ProtectionLevel.everyone, c.getTargetEmail(), c.getTargetEmail());

//                        Connection c2 = ConnectionFactory.getInstance(entity, c.getOwner());
                        Connection c2 = new ConnectionModel.Builder()
                                .targetEmail(c.getOwner())
                                .approved(true)
                                .create();


                        EmailAddress emailAddress = CommonFactory.createEmailAddress(c.getTargetEmail());
                        // List<User> userSample = userService.getUserByKey(emailAddress.getValue(), AuthLevel.restricted);
                        if (userExists(emailAddress.getValue())) {
                            Optional<User> optional = userService.getUserByKey(emailAddress.getValue());
                            if (optional.isPresent()) {
                                entityService.addUpdateEntity(valueService, optional.get(), c2);
                                success = true;
                            }
                            else {
                                success = false;
                            }
                        }

                    }
                }

            }


        }
        return success;

    }

    @Override
    public Boolean verifyEmail(String email) {
        return userExists(email);
    }

    @Override
    public SystemDetails getSystemDetails() {
        log.info("Get System Details: " + (authService == null));
        return new SystemDetailsModel(Const.VERSION, authService.isGAE());
    }

}

