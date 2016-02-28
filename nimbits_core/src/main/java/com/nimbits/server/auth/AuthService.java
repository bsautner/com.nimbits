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

package com.nimbits.server.auth;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AuthService {

    List<EmailAddress> getCurrentUser(EntityService entityService, UserService userService, ValueService valueService, HttpServletRequest request);

    boolean isGAE();

    boolean isGAEAdmin();

    String createLoginURL(String requestUri);

    String createLogoutURL(String requestUri);

    Transport getMailTransport();
}
