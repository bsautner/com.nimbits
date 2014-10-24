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
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.user.UserRpcService;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("userRpcService")
public class UserRpcServiceImpl extends RemoteServiceServlet implements UserRpcService {


    @Autowired
    private EntityService entityService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private UserService userService;




    @Override
    public User loginRpc(final String requestUri) {

        final User retObj;
        EmailAddress internetAddress;
        String admin = settingsService.getSetting(ServerSetting.admin);
        internetAddress = CommonFactory.createEmailAddress(admin);


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
