/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.google.appengine.tools.development.testing.*;
import com.google.gwt.benchmarks.client.Setup;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/4/12
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class TimeServletTest  {
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
        TimeServlet t = new TimeServlet();
        t.doGet(req, resp);

        String r = resp.getContentAsString();
        assertNotNull(r);
        long l = Long.valueOf(r);
        assertEquals(new Date().getTime(), l);//fails if things are slow



    }
    @Test
    public void testGetTimeArduino() throws IOException, ServletException {
        TimeServlet t = new TimeServlet();
        req.addParameter(Parameters.client.getText(), ClientType.arduino.getCode());
        t.doGet(req, resp);

        String r = resp.getContentAsString();
        assertNotNull(r);

        assertTrue(r.endsWith(">"));
        assertTrue(r.startsWith("<"));


    }


}
