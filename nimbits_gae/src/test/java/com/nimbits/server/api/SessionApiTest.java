package com.nimbits.server.api;

import com.nimbits.server.NimbitsServletTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertTrue;


public class SessionApiTest extends NimbitsServletTest {

    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
        super.setup();
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();
    }

    @Test
    public void testDoGet() throws Exception {
        assertTrue(true);


    }
}
