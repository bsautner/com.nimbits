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

import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
public class SystemCronTest extends NimbitsServletTest {




    @Test
    public void doGetTest() throws InterruptedException {

        try {
        systemCron.doGet(req, resp);
        Thread.sleep(2000);
        Map<SettingType, String> settings = settingsService.getSettings();

        for (SettingType setting : SettingType.values()) {
            if (setting.isCreate()) {
                assertEquals(setting.getDefaultValue(), settingsService.getSetting(setting));
            }
        }
        } catch (IOException e) {
            fail();
        } catch (NimbitsException e) {
            e.printStackTrace();
            fail();
        }

    }
}
