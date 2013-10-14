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

package com.nimbits.cloudplatform.server.transactions.settings;

import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.process.cron.SystemCron;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;


public class SettingDaoImplTest extends NimbitsServletTest {


    SystemCron systemCron = new SystemCron();

    @Test
    public void getSettingsTest() throws  Exception {


        systemCron.doGet(req, resp);
        Thread.sleep(2000);
        Map<String, String> settings = SettingFactory.getServiceInstance().getSettings();
        assertTrue(settings.size() > 0);

    }
}
