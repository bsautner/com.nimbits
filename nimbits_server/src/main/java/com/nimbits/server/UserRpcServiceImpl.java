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

import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.client.service.user.AbstractUserRpcService;
import com.nimbits.client.service.user.UserRpcService;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.List;

@Service("userRpcService")
public class UserRpcServiceImpl extends AbstractUserRpcService implements UserRpcService {

    private static final String ANON_NIMBITS_COM = "anon@nimbits.com";
    public static final String UNAUTHORISED = "unauthorised";

    private UserService userService;


    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public User loginRpc(final String requestUri) throws Exception {
        User retObj;
        if (getThreadLocalRequest().getSession() != null) {
            String email = (String)  getThreadLocalRequest().getSession().getAttribute("LOGGED_IN_EMAIL");
            if (email != null) {
                List<User> users = userService.getUserByKey(email, AuthLevel.readWriteAll);
                if (users.isEmpty()) {
                    throw new Exception(UNAUTHORISED);

                }
                else {
                    retObj = users.get(0);
                }
            }
            else {
                throw new Exception(UNAUTHORISED);
            }
        }
        else {
            final EntityName name = CommonFactory.createName(ANON_NIMBITS_COM, EntityType.user);
            final Entity e = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe, "", "");
            retObj = UserModelFactory.createUnauthenticatedUserModel(e);
            retObj.setLoggedIn(false);

        }
        return retObj;





    }


}
