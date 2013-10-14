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

import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by benjamin on 9/1/13.
 */
public interface UserService {
    EmailAddress getEmail();

    User getHttpRequestUser(HttpServletRequest req);

    void getEmailFromRequest(HttpServletRequest req);

    AccessKey authenticatedKey(Entity user);

    User createUserRecord(EmailAddress internetAddress);

    User getAdmin();

    List<User> getUserByKey(String key, AuthLevel authLevel);

    User getAnonUser();
}
