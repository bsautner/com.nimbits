/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.settings;

import com.nimbits.client.enums.ServerSetting;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transaction.settings.SettingsService;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;


public class SettingDaoImplTest extends NimbitsServletTest {
    @Resource(name = "settingsService")
    SettingsService settingsService;

    @Test
    public void getSettingsTest() throws Exception {

        Thread.sleep(2000);
        for (ServerSetting types : ServerSetting.values()) {
            String settings = settingsService.getSetting(types);
            assertNotNull(settings);
        }

    }
}
