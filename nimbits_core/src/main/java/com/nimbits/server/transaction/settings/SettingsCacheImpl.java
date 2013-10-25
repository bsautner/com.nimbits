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

import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.enums.SettingType;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.cache.NimbitsCache;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 10:44 AM
 */

public class SettingsCacheImpl implements SettingsService {
    private NimbitsEngine engine;
    private SettingsService dao;
    private final NimbitsCache cache;
    public SettingsCacheImpl(NimbitsEngine engine) {
        this.engine = engine;
        cache = engine.getCache();
        this.dao = SettingServiceFactory.getDaoInstance(engine);
    }

   

    @Override
    public String getSetting(final SettingType setting)  {


        if (cache.containsKey((setting.name()))) {
            return (String) cache.get((setting.name()));

        } else {
            String storedVal = dao.getSetting(setting);
            cache.put((setting.name()), storedVal);
            return storedVal;
        }


    }




    @Override
    public void addSetting(final SettingType setting, final String value)  {
        dao.addSetting(setting, value);


        if (cache.containsKey(MemCacheKey.allSettings.getText())) {
            cache.remove(MemCacheKey.allSettings.getText());
        }
        if (cache.containsKey((setting.name()))) {
            cache.remove((setting.name()));
        }


    }


    @Override
    public void updateSetting(final SettingType setting, final String newValue)  {
        dao.updateSetting(setting, newValue);

        if (cache.containsKey(MemCacheKey.allSettings.getText())) {
            cache.remove(MemCacheKey.allSettings.getText());
        }
        if (cache.containsKey((setting.name()))) {
            cache.remove((setting.name()));
        }

    }



}
