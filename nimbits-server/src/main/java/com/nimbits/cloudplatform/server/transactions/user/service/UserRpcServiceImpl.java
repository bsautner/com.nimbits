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

package com.nimbits.cloudplatform.server.transactions.user.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModelFactory;
import com.nimbits.cloudplatform.client.service.user.UserService;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.user.UserServiceFactory;
import com.nimbits.cloudplatform.server.transactions.user.cache.UserCache;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/2/13
 * Time: 6:27 PM
 */
@Service("userService")
public class UserRpcServiceImpl extends RemoteServiceServlet implements UserService {

    public static final String ANON_NIMBITS_COM = "anon@nimbits.com";
    private final EntityService entityService = EntityServiceFactory.getInstance();
    private final UserCache userCache = UserServiceFactory.getCacheInstance();
    @Override
    public User loginRpc(final String requestUri) {

        final User retObj;
        EmailAddress internetAddress = null;
        boolean isAdmin = false;



        final com.google.appengine.api.users.UserService userService = com.google.appengine.api.users.UserServiceFactory.getUserService();



            final com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
            if (googleUser != null) {
                isAdmin = userService.isUserAdmin();
                internetAddress = CommonFactory.createEmailAddress(googleUser.getEmail());
            }


        if (internetAddress != null) {

            final List<Entity> list = entityService
                    .getEntityByKey(
                            UserServiceFactory.getInstance().getAnonUser(), internetAddress.getValue(), EntityType.user);


            if (list.isEmpty()) {

                retObj = UserServiceFactory.getInstance().createUserRecord(internetAddress);

            } else {
                retObj = (User) list.get(0);
            }

            retObj.setLoggedIn(true);

            retObj.setUserAdmin(isAdmin);

            retObj.setLogoutUrl(userService.createLogoutURL(requestUri));

            retObj.setLastLoggedIn(new Date());
            entityService.addUpdateEntity(retObj, Arrays.<Entity>asList(retObj));
            retObj.addAccessKey(UserServiceFactory.getInstance().authenticatedKey(retObj));


        } else {
            final EntityName name = CommonFactory.createName(ANON_NIMBITS_COM, EntityType.user);
            final Entity e = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe, "", "");
            retObj = UserModelFactory.createUserModel(e);
            retObj.setLoggedIn(false);
            retObj.setLoginUrl(userService.createLoginURL(requestUri));
        }

        HttpSession session = getThreadLocalRequest().getSession();
        if (session != null) {
            retObj.setSessionId(session.getId());
            userCache.cacheAuthenticatedUser(session.getId(), retObj);
        }

        return retObj;
    }






}
