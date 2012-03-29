package com.nimbits.server.cron;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.quota.*;
import com.nimbits.server.settings.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:24 PM
 */
public class QuotaResetCronTest extends NimbitsServletTest {


    @Test
    public void testQuotaReset() throws IOException, NimbitsException {

        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);


        for (int i = 0; i < 10; i++) {
            valueServlet.processGet(req, resp);
        }
        assertEquals(10, QuotaFactory.getInstance(user).getCount());

        QuotaResetCron cron = new QuotaResetCron();
        cron.doGet(req,resp);
        assertEquals(0, QuotaFactory.getInstance(user).getCount());


    }

}
