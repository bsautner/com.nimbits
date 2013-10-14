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
import com.nimbits.cloudplatform.client.service.settings.SettingsService;
import com.nimbits.cloudplatform.server.transactions.datastore.NimbitsStore;
import com.nimbits.cloudplatform.server.transactions.datastore.StoreFactory;
import net.sf.jsr107cache.CacheException;

import java.util.HashMap;
import java.util.Map;

import static com.nimbits.cloudplatform.server.transactions.settings.SettingFactory.getCacheInstance;


public class SettingsServiceImpl extends RemoteServiceServlet implements SettingsService {
    private Settings settingsCache;

    {
        try {
            NimbitsStore store = StoreFactory.getInstance();
            settingsCache = getCacheInstance(store);

        } catch (CacheException e) {
            settingsCache = null; //TODO getEmptyInstance();
        }
    }

    @Override
    public HashMap<String, String> getSettingsRpc() {
        return settingsCache.getSettings();
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
    @Override
    public void addSetting(String setting, boolean defaultValue) {
        addSetting(setting, defaultValue ? Const.TRUE : Const.FALSE);
    }

}
