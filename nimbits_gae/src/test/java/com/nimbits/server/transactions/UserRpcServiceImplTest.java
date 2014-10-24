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

package com.nimbits.server.transactions;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.user.UserRpcService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.UserRpcServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class UserRpcServiceImplTest extends NimbitsServletTest {

    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("example.com");


    @Before
    public void setup() {
        super.setup();
        helper.setUp();
    }

    @After
    public void after() {
        helper.tearDown();

    }

    @Test
    public void loginRpcTest() {
        UserRpcService service = new UserRpcServiceImpl();
        User user = service.loginRpc("foo.bar");
        assertNotNull(user);

    }


}
