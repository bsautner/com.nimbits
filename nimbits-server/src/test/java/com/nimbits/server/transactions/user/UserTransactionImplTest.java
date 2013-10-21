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

import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.transaction.user.service.UserService;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by benjamin on 9/1/13.
 */
public class UserTransactionImplTest extends NimbitsServletTest{
    @Test
    public void testGetHttpRequestUser() throws Exception {
        UserService t = AuthenticationServiceFactory.getInstance(engine);
        t.getHttpRequestUser(req);
        assertEquals(user.getEmail(), t.getHttpRequestUser(req).getEmail());


    }



    @Test
    public void testGetEmailFromRequest() throws Exception {
        UserService t = AuthenticationServiceFactory.getInstance(engine);
        t.getEmailFromRequest(req);
        assertEquals(user.getEmail(), t.getEmail());
    }

    @Test
    public void testAuthenticatedKey() throws Exception {

    }

    @Test
    public void testCreateUserRecord() throws Exception {

    }

    @Test
    public void testGetAdmin() throws Exception {

    }

    @Test
    public void testGetUserByKey() throws Exception {

    }

    @Test
    public void testGetAnonUser() throws Exception {

    }
}
