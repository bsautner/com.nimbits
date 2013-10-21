package com.nimbits.server.api;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Created by benjamin on 9/3/13.
 */
public class ApiBaseTest {
    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;
    ApiBase impl = new ApiBase();
    @Before
    public void setup() {
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();
    }



    @Test
    public void testSetup() throws Exception {

    }

    @Test
    public void testCompleteResponse() throws Exception {

    }

    @Test
    public void testReadJson() throws Exception {

    }

    @Test
    public void testGetContent() throws Exception {

    }

    @Test
    public void testGetUser() throws Exception {

    }

    @Test
    public void testGetEntity() throws Exception {

    }

    @Test
    public void testSendError() throws Exception {

    }

    @Test
    public void testAddHeaders() throws Exception {

    }
}
