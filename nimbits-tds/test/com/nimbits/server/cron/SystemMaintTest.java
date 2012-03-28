package com.nimbits.server.cron;

import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.api.impl.*;
import com.nimbits.server.user.*;
import org.junit.*;
import static org.junit.Assert.fail;
import org.springframework.mock.web.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 4:34 PM
 */
public class SystemMaintTest {

    String email = "test@test.com";
    User user;
    EmailAddress em;
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    UserTransactions dao;
    ValueServletImpl servlet;
    MockHttpServletRequest req;
    MockHttpServletResponse resp;

    @Before
    public void setUp() throws NimbitsException {

        helper.setUp();
        em = CommonFactoryLocator.getInstance().createEmailAddress(email);
        servlet= new ValueServletImpl();
        req = new MockHttpServletRequest();

        resp = new MockHttpServletResponse();
        dao = UserTransactionFactory.getDAOInstance();
        dao.createNimbitsUser(CommonFactoryLocator.getInstance().createEmailAddress("test@example.com"));
        dao.createNimbitsUser(em);
        user = dao.getNimbitsUser(em);
        req.addParameter(Parameters.email.getText(), email);
        req.addParameter(Parameters.secret.getText(),user.getSecret() );
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }


    @Test
    public void doGetTest() {
        SystemMaint systemMaint = new SystemMaint();
        try {
            systemMaint.doGet(req, resp);
        } catch (IOException e) {
            fail();
        }

    }
}
