package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.client.enums.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * Created by benjamin on 9/3/13.
 */
public class SessionApiTest {

    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;
    SessionApi impl = new SessionApi();
    @Before
    public void setup() {
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();
    }

    @Test
    public void testDoGet() throws Exception {
         req1.addParameter("email", "test@example.com");
         req1.addHeader(Parameters.apikey.getText(), "some key");
         impl.doGet(req1, resp1);
         assertEquals(HttpServletResponse.SC_OK, resp1.getStatus());


    }
}
