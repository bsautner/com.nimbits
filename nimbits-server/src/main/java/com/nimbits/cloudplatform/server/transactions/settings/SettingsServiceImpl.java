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


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.client.service.settings.SettingsService;
import com.nimbits.cloudplatform.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("settingsService")
public class SettingsServiceImpl extends RemoteServiceServlet implements SettingsService {
    @Override
    public HashMap<String, String> getSettingsRpc() {
        return SettingsCache.getSettings();
    }

    public static Map<String, String> getSettings() {
        return SettingsCache.getSettings();
    }


    public static String getSetting(final String paramName)  {
        return SettingsCache.getSetting(paramName);
    }

    public static void updateSetting(final String setting, final String newValue) {
        SettingsCache.updateSetting(setting, newValue);
    }

    public static void addSetting(final String setting, final String value) {
        SettingsCache.addSetting(setting, value);
    }


    public static void addSetting(String setting, boolean defaultValue) {
        addSetting(setting, defaultValue ? Const.TRUE : Const.FALSE);
    }


    public static String reloadCache() {
        return SettingsCache.reloadCache();
    }





}
