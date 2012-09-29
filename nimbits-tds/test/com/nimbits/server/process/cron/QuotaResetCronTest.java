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

package com.nimbits.server.process.cron;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.admin.quota.QuotaManager;
import com.nimbits.server.api.impl.ValueServletImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:24 PM
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
public class QuotaResetCronTest extends NimbitsServletTest {
    @Resource(name = "value")
    ValueServletImpl valueServlet;

    @Resource(name="systemCron")
    SystemCron systemCron;

    @Resource(name="quotaCron")
    QuotaResetCron quotaResetCron;

    @Resource(name="quotaManager")
    QuotaManager quotaManager;

    @Test
    public void testQuotaReset() throws IOException, NimbitsException {



        systemCron.doGet(req, resp);
        settingsService.updateSetting(SettingType.billingEnabled, Const.TRUE);


        for (int i = 0; i < 10; i++) {
            valueServlet.doGet(req, resp);
        }
        assertEquals(10,quotaManager.getCount(user.getEmail()));


        quotaResetCron.doGet(req,resp);
        assertEquals(0, quotaManager.getCount(user.getEmail()));


    }

}
