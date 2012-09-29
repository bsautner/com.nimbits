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

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.admin.quota.QuotaManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:52 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class ValueServletImplTest extends NimbitsServletTest {
    @Resource(name = "value")
    ValueServletImpl valueServlet;

    @Resource(name = "valueService")
    ValueService valueService;

    @Resource(name = "settingsService")
    SettingsService settingsService;


    @Resource(name="quotaManager")
    QuotaManager quotaManager;

    @Test
    @Ignore
    public void testPostData() throws NimbitsException, InterruptedException, IOException, ServletException {

        req.removeAllParameters();
        req.addParameter("point", pointName.getValue());
        req.addParameter("data", "Medication");
        req.addParameter("timestamp", "1336579929000");
        req.addParameter("value", "5");
        ValueServletImpl i = new ValueServletImpl();
        i.handleRequest(req, resp);

        List<Value> v = valueService.getCurrentValue(point);
        assertNotNull(v);
        assertFalse(v.isEmpty());
        assertEquals(5.0, v.get(0).getDoubleValue(), 0.001);
        assertEquals("Medication",  v.get(0).getData().getContent());
        assertEquals( v.get(0).getTimestamp().getTime(), new Date(1336579929000L).getTime());

    }




    @Test
    public void arduinoTest() throws IOException {


        req.addParameter(Parameters.client.getText(), ClientType.arduino.getCode());
        valueServlet.doGet(req, resp);
        String s = resp.getContentAsString();


        assertNotNull(s);
        assertTrue(s.startsWith(Const.CONST_ARDUINO_DATA_SEPARATOR));
        assertTrue(s.endsWith(Const.CONST_ARDUINO_DATA_SEPARATOR));


    }
    @Test
    public void testQuotaException() throws IOException, NimbitsException {


        systemCron.doGet(req, resp);
        settingsService.updateSetting(SettingType.billingEnabled, Const.TRUE);


        for (int i = 0; i < quotaManager.getFreeDailyQuota()+10; i++) {
            req.setMethod("GET");
            valueServlet.doGet(req, resp);
            if (i < quotaManager.getFreeDailyQuota()) {
              assertEquals(resp.getStatus(),  HttpServletResponse.SC_OK);

            }
            else {
                assertEquals(resp.getStatus(),  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }



    }
    @Test
    public void testNoQuotaExceptionOnServerWithQuotaDisabled() throws IOException, NimbitsException {


        systemCron.doGet(req, resp);
        settingsService.updateSetting(SettingType.billingEnabled, Const.FALSE);


        for (int i = 0; i <quotaManager.getFreeDailyQuota() +10; i++) {
            valueServlet.doGet(req, resp);
        }
        assertTrue(true);


    }


    @Test
    public void processRequestTest() throws NimbitsException {
        Value v = ValueFactory.createValueModel(1.2);

        String j = valueServlet.processRequest(pointName.getValue(), null, "double", v, user);

        assertEquals(1.2,Double.valueOf(j), 0.001);
        String c = valueServlet.processRequest(pointName.getValue(), null, "double", null, user);
        assertEquals(1.2,Double.valueOf(c), 0.001);


    }




}
