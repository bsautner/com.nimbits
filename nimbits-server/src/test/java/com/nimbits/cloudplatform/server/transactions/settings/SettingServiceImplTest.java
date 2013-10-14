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

package com.nimbits.cloudplatform.server.transactions.settings;

import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.client.service.settings.SettingsService;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.process.cron.SystemCron;
import com.nimbits.cloudplatform.server.transactions.cache.CacheFactory;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by benjamin on 10/9/13.
 */
public class SettingServiceImplTest  extends NimbitsServletTest {


    SystemCron systemCron = new SystemCron();
    SettingsService service = SettingFactory.getServiceInstance();
    @Test
    public void getSettingsTest() throws  Exception {
        systemCron.doGet(req, resp);
        Thread.sleep(2000);
        Map<String, String> settings = service.getSettings();
        assertTrue(settings.size() > 0);
        String admin = service.getSetting(SettingType.admin.getName());
        assertNotNull(admin);
        System.out.println(admin);
        NimbitsCache cache = CacheFactory.getInstance();
        assertTrue(cache.confirmCached(SettingType.admin.getName()));

    }
}
