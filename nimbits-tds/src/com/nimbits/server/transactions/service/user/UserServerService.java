/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.service.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 4/17/11
 * Time: 7:17 PM
 */
public interface UserServerService {
    User getHttpRequestUser(final HttpServletRequest req) throws NimbitsException;

    User getAdmin() throws NimbitsException;

    User getAnonUser();

    User createUserRecord(final EmailAddress internetAddress) throws NimbitsException;

    AccessKey authenticatedKey(final Entity user) throws NimbitsException;


    void fundAccount(User user, BigDecimal amount) throws NimbitsException;
}
