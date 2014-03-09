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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.user.UserService;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingServiceFactory;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.transaction.user.cache.UserCache;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class UserRpcServiceImpl extends RemoteServiceServlet implements  UserService{


    NimbitsEngine engine = ApplicationListener.createEngine();
    private final EntityService entityService = EntityServiceFactory.getInstance(engine);
    private final SettingsService settingsService = SettingServiceFactory.getServiceInstance(engine);

    @Override
    public User loginRpc(final String requestUri) {

        final User retObj;
        EmailAddress internetAddress;
        String admin = settingsService.getSetting(SettingType.admin);
        internetAddress = CommonFactory.createEmailAddress(admin);


        com.nimbits.server.transaction.user.service.UserService userService = AuthenticationServiceFactory.getInstance(engine);
            final List<Entity> list = entityService
                    .getEntityByKey(
                            userService.getAnonUser(), internetAddress.getValue(), EntityType.user);


            if (list.isEmpty()) {

                retObj = userService.createUserRecord(internetAddress);

            } else {
                retObj = (User) list.get(0);
            }

            retObj.setLoggedIn(true);

            retObj.setUserAdmin(true);

            retObj.setLogoutUrl("");

            retObj.setLastLoggedIn(new Date());
            entityService.addUpdateEntity(retObj, Arrays.<Entity>asList(retObj));
            retObj.addAccessKey(userService.authenticatedKey(retObj));



        return retObj;
    }






}
