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

package com.nimbits.cloudplatform.server.transactions.user;

import com.google.appengine.api.users.UserServiceFactory;
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
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
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

    @Override
    public User loginRpc(final String requestUri) throws Exception {

        final User retObj;
        EmailAddress internetAddress = null;
        boolean isAdmin = false;



        final com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();



            final com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
            if (googleUser != null) {
                isAdmin = userService.isUserAdmin();
                internetAddress = CommonFactory.createEmailAddress(googleUser.getEmail());
            }


        if (internetAddress != null) {

            final List<Entity> list = EntityServiceImpl
                    .getEntityByKey(
                            UserTransaction.getAnonUser(), internetAddress.getValue(), EntityType.user);


            if (list.isEmpty()) {

                retObj = UserTransaction.createUserRecord(internetAddress);

            } else {
                retObj = (User) list.get(0);
            }

            retObj.setLoggedIn(true);

            retObj.setUserAdmin(isAdmin);

            retObj.setLogoutUrl(userService.createLogoutURL(requestUri));

            retObj.setLastLoggedIn(new Date());
            EntityServiceImpl.addUpdateEntity(retObj, Arrays.<Entity>asList(retObj));
            retObj.addAccessKey(UserTransaction.authenticatedKey(retObj));


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
            UserCache.cacheAuthenticatedUser(session.getId(), retObj);
        }

        return retObj;
    }






}
