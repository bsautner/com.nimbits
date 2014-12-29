/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.client.service.user.UserRpcService;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service("userRpcService")
public class UserRpcServiceImpl  extends RemoteServiceServlet implements UserRpcService  {

    private static final String ANON_NIMBITS_COM = "anon@nimbits.com";
    private static final Logger log = Logger.getLogger(UserRpcServiceImpl.class.getName());
    protected static final String LOGOUT_URL = "/service/v2/session?action=logout";

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;


    @Override
    public User loginRpc(final String requestUri) {

        final User retObj;
        EmailAddress internetAddress = null;
        boolean isAdmin = false;


        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();
        if (gaeUserService == null) {
            throw new SecurityException("Google Login Service Unavailable");
        } else {

            final com.google.appengine.api.users.User googleUser = gaeUserService.getCurrentUser();
            if (googleUser != null) {
                isAdmin = gaeUserService.isUserAdmin();
                internetAddress = CommonFactory.createEmailAddress(googleUser.getEmail());
            }

            if (internetAddress != null) {

                log.info("getting user with address: " + internetAddress.getValue());
                final List<Entity> list = entityService.getUserEntity(internetAddress);

                if (list.isEmpty()) {
                    log.info("user not found, creating record");
                    retObj = userService.createUserRecord(internetAddress, UUID.randomUUID().toString(), UserSource.google);

                } else {
                    log.info("got user result");
                    retObj = (User) list.get(0);
                }

                retObj.setLoggedIn(true);

                retObj.setIsAdmin(isAdmin);

                retObj.setLogoutUrl(gaeUserService.createLogoutURL(requestUri));

                retObj.setLastLoggedIn(new Date());
                entityService.addUpdateEntity(retObj, Arrays.<Entity>asList(retObj));
                retObj.addAccessKey(userService.authenticatedKey(retObj));


            } else {
                final EntityName name = CommonFactory.createName(ANON_NIMBITS_COM, EntityType.user);
                final Entity e = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe, "", "");
                retObj = UserModelFactory.createUnauthenticatedUserModel(e);
                retObj.setLoggedIn(false);
                retObj.setLoginUrl(gaeUserService.createLoginURL(requestUri));
            }
//            if (getThreadLocalRequest() != null) {
//                HttpSession session = getThreadLocalRequest().getSession();
//                if (session != null) {
//                    retObj.setSessionId(session.getId());
//                    userCache.cacheAuthenticatedUser(session.getId(), retObj);
//                }
//            }

            return retObj;


        }
    }

    @Override
    public User doLogin(String email, String password) throws Exception {
        List<User> userList = userService.getUserByKey(email, AuthLevel.readWriteAll);
        if (userList.isEmpty()) {
            throw new Exception("User Not Found.");
        }
        else {
            User user = userList.get(0);
            boolean okPassword = user.getPassword().equals(DigestUtils.sha512Hex(password + user.getPasswordSalt()));
            if (okPassword) {
                user.setLoggedIn(true);
                user.setLogoutUrl(LOGOUT_URL);
                return user;
            }
            else {
                throw new Exception("Invalid user name or password");


            }
        }


    }


    @Override
    public User register(String email, String password) throws Exception {
        EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
        List<User> userList = userService.getUserByKey(email, AuthLevel.restricted);


        if (userList.isEmpty()) {

            User user = userService.createUserRecord(emailAddress, password, UserSource.local);
            user.setLoggedIn(true);


            user.setLogoutUrl(LOGOUT_URL);
            return user;

        }
        else {
            throw new Exception("A user with that email is already registered on this system");


        }

    }

}

