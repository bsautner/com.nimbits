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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class UserDaoTest extends NimbitsServletTest {
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());
    //new LocalTaskQueueTestConfig(),
    //new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("example.com");

    @Before
    public void setup() {

        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();

    }

    @Test
    public void createUserTest() throws Exception {
        EmailAddress e = CommonFactory.createEmailAddress("bob@example.com");
        User u = AuthenticationServiceFactory.getInstance(engine).createUserRecord(e);
        assertNotNull(u);
        assertEquals(e.getValue(), u.getEmail().getValue());
        List<Entity> result = entityService.getEntityByKey(u, e.getValue(), EntityType.user);
        assertFalse(result.isEmpty());


        User r = (User) result.get(0);


        assertNotNull(r);
        assertEquals(e.getValue(), r.getEmail().getValue());
        assertNotNull(r.getDateCreated());


    }

}
