/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.cron;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.*;
import com.nimbits.server.quota.*;
import com.nimbits.server.settings.*;
import static org.junit.Assert.*;
import org.junit.*;

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
        assertEquals(10, QuotaFactory.getInstance(user.getEmail()).getCount());

        QuotaResetCron cron = new QuotaResetCron();
        cron.doGet(req,resp);
        assertEquals(0, QuotaFactory.getInstance(user.getEmail()).getCount());


    }

}
