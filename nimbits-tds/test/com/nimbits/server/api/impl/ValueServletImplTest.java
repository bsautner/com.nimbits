package com.nimbits.server.api.impl;

import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.server.api.impl.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.cron.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.settings.*;
import com.nimbits.server.user.*;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.springframework.mock.web.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:52 PM
 */
public class ValueServletImplTest {
    String email = "test@test.com";
    EntityName name;
    User user;
    EmailAddress em;
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    UserTransactions dao;
    ValueServletImpl servlet;
    MockHttpServletRequest req;
    MockHttpServletResponse resp;
    PointService pointService;
    @Before
    public void setUp() throws NimbitsException {

        helper.setUp();
        pointService = PointServiceFactory.getInstance();
        em = CommonFactoryLocator.getInstance().createEmailAddress(email);
        servlet= new ValueServletImpl();
        req = new MockHttpServletRequest();
        name = CommonFactoryLocator.getInstance().createName("point", EntityType.point);
        resp = new MockHttpServletResponse();
        dao = UserTransactionFactory.getDAOInstance();
        dao.createNimbitsUser(CommonFactoryLocator.getInstance().createEmailAddress("test@example.com"));
        dao.createNimbitsUser(em);
        user = dao.getNimbitsUser(em);
        req.addParameter(Parameters.email.getText(), email);
        req.addParameter(Parameters.secret.getText(),user.getSecret() );
        req.addParameter(Parameters.point.getText(), name.getValue());
        req.addParameter(Parameters.value.getText(), "1.234");
        Entity e = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(), user.getUuid(), user.getUuid());

        pointService.addPoint(user, e);

    }

    @After
    public void tearDown() {
        helper.tearDown();
    }



    @Test
    public void doGetTest() {


        try {
            servlet.doGet(req, resp);
        } catch (IOException e) {
            fail();
        }

    }


    @Test
    (expected=NimbitsException.class)
    public void testQuotaException() throws IOException, NimbitsException {

        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);


        for (int i = 0; i < Const.MAX_DAILY_QUOTA+10; i++) {
            servlet.processGet(req, resp);
        }



    }
    @Test
    public void testNoQuotaExceptionOnServerWithQuotaDisabled() throws IOException, NimbitsException {

        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.FALSE);


        for (int i = 0; i < Const.MAX_DAILY_QUOTA+10; i++) {
            servlet.processGet(req, resp);
        }
        assertTrue(true);


    }


}
