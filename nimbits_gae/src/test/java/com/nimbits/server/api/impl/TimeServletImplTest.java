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

package com.nimbits.server.api.impl;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;



public class TimeServletImplTest {
    public MockHttpServletRequest req;
    public MockHttpServletResponse resp;

    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalUserServiceTestConfig()).setEnvIsLoggedIn(false);


    @Before
    public void setup() {
        helper.setUp();
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();
    }


    @Test
    public void testGetTime() throws IOException, ServletException {
        TimeServletImpl t = new TimeServletImpl();
        t.doGet(req, resp);

        String r = resp.getContentAsString();
        assertNotNull(r);
        long l = Long.valueOf(r);
        assertEquals(new Date().getTime() / 1000, l / 1000);//fails if things are slow


    }

    @Test
    public void testGetTimeArduino() throws IOException, ServletException {
        TimeServletImpl t = new TimeServletImpl();
        req.addParameter(Parameters.client.getText(), ClientType.arduino.getCode());
        t.doGet(req, resp);

        String r = resp.getContentAsString();
        assertNotNull(r);

        assertTrue(r.endsWith(Const.CONST_ARDUINO_DATA_SEPARATOR));
        assertTrue(r.startsWith(Const.CONST_ARDUINO_DATA_SEPARATOR));


    }


}
