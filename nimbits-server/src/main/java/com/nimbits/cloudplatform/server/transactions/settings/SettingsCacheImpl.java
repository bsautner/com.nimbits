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

import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.datastore.NimbitsStore;

import java.util.HashMap;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 10:44 AM
 */

public class SettingsCacheImpl implements Settings {
    private NimbitsCache cache;
    private Settings dao;
    public SettingsCacheImpl(NimbitsCache cache, NimbitsStore store) {
        this.cache = cache;
        this.dao = new SettingsDaoImpl(store);
    }

   

    @Override
    public String getSetting(final String setting)  {


        if (cache.containsKey((setting))) {
            return (String) cache.get((setting));

        } else {
            String storedVal = dao.getSetting(setting);
            cache.put((setting), storedVal);
            return storedVal;
        }


    }


    @Override
    public HashMap<String, String> getSettings()  {


        return dao.getSettings();


    }


    @Override
    public void addSetting(final String setting, final String value)  {
        dao.addSetting(setting, value);


        if (cache.containsKey(MemCacheKey.allSettings.getText())) {
            cache.remove(MemCacheKey.allSettings.getText());
        }
        if (cache.containsKey((setting))) {
            cache.remove((setting));
        }


    }


    @Override
    public void updateSetting(final String setting, final String newValue)  {
        dao.updateSetting(setting, newValue);

        if (cache.containsKey(MemCacheKey.allSettings.getText())) {
            cache.remove(MemCacheKey.allSettings.getText());
        }
        if (cache.containsKey((setting))) {
            cache.remove((setting));
        }

    }



}
