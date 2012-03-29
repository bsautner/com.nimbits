package com.nimbits.server.api.impl;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.cron.*;
import com.nimbits.server.settings.*;
import helper.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:52 PM
 */
public class ValueServletImplTest extends NimbitsServletTest {




    @Test
    public void doGetTest() {


        try {
            valueServlet.doGet(req, resp);
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
            valueServlet.processGet(req, resp);
        }



    }
    @Test
    public void testNoQuotaExceptionOnServerWithQuotaDisabled() throws IOException, NimbitsException {

        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.FALSE);


        for (int i = 0; i < Const.MAX_DAILY_QUOTA+10; i++) {
            valueServlet.processGet(req, resp);
        }
        assertTrue(true);


    }


}
