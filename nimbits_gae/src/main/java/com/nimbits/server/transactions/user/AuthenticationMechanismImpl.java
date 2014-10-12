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

package com.nimbits.server.transactions.user;

import com.google.appengine.api.users.UserServiceFactory;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuthenticationMechanismImpl {
    private com.google.appengine.api.users.UserService googleUserService;

    public AuthenticationMechanismImpl() {
        googleUserService = UserServiceFactory.getUserService();
    }


    public List<EmailAddress> getCurrentUserEmail() {
        if (this.googleUserService != null) {
            this.googleUserService = UserServiceFactory.getUserService();
            com.google.appengine.api.users.User appUser = googleUserService.getCurrentUser();
            if (appUser != null) {
                String email = googleUserService.getCurrentUser().getEmail();
                if (!StringUtils.isEmpty(email)) {
                    return Arrays.asList(CommonFactory.createEmailAddress(email));
                }

            }

        }
        return Collections.emptyList();
    }
}
