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

import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import helper.NimbitsServletTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 4:34 PM
 */
public class SystemMaintTest extends NimbitsServletTest {




    @Test
    public void doGetTest() throws InterruptedException {
        SystemMaint systemMaint = new SystemMaint();
        try {
        systemMaint.doGet(req, resp);
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
