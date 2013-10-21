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

package com.nimbits.server.transaction.settings;


import com.nimbits.server.NimbitsEngine;

import java.util.Map;


public class SettingsServiceImpl implements SettingsService {

    private SettingsService settingsCache;
    public SettingsServiceImpl(NimbitsEngine engine) {
            settingsCache = SettingServiceFactory.getCacheInstance(engine);

    }

    @Override
    public Map<String, String> getSettings() {
        return settingsCache.getSettings();
    }
    @Override
    public String getSetting(final String paramName)  {
        return settingsCache.getSetting(paramName);
    }

    @Override
    public void updateSetting(final String setting, final String newValue) {
        settingsCache.updateSetting(setting, newValue);
    }
    @Override
    public void addSetting(final String setting, final String value) {
        settingsCache.addSetting(setting, value);
    }


}
