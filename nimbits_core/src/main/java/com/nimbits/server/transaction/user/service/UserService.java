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
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.Credentials;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.http.HttpServletRequest;


public interface UserService {

    Optional<Credentials> credentialsWithBasicAuthentication(HttpServletRequest req);

    User getHttpRequestUser(final EntityService entityService, final ValueService valueService, HttpServletRequest req);

    EmailAddress getEmailFromRequest(HttpServletRequest req);


    User createUserRecord(final EntityService entityService, final ValueService valueService, EmailAddress internetAddress, String password, UserSource source);

    User getAdmin();

    Optional<User> getUserByKey(String key);

    boolean validatePassword(final EntityService entityService, final ValueService valueService, User user, String password);

    User doLogin(final EntityService entityService, final ValueService valueService, HttpServletRequest threadLocalRequest, String email, String password);

    String startSession(HttpServletRequest threadLocalRequest, String email);

    void setResetPasswordToken(User user, String token);

    boolean userHasPoints(User user);

    User updatePassword(User u, String password);

    String getToken(HttpServletRequest req);


}
