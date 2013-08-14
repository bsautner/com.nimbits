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

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.enums.SettingType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 10:44 AM
 */

public class SettingsCache {
    private static MemcacheService cacheFactory;

    static {
        cacheFactory = MemcacheServiceFactory.getMemcacheService();
    }


    protected static String SettingCacheKey(final String setting) {
        return SettingType.serverVersion.getDefaultValue() + MemCacheKey.setting + setting;
    }


    protected static String reloadCache()  {

        final StringBuilder builder = new StringBuilder(1024);
        builder.append("<h5>Removing old values from memcache</h5>");

        final Map<String, String> settings =SettingsDao.getSettings();
        if (cacheFactory.contains(MemCacheKey.allSettings)) {
            cacheFactory.delete(MemCacheKey.allSettings);
        }
        for (final String setting : settings.keySet()) {

            if (setting != null) {

                builder.append("Removed: ").append(setting ).append("<br />");
                if (cacheFactory.contains(SettingCacheKey(setting)))   {
                    cacheFactory.delete(SettingCacheKey(setting));
                }
            }



        }


        return builder.toString();

    }



    protected static String getSetting(final String setting)  {


        if (cacheFactory.contains(SettingCacheKey(setting))) {
            return (String) cacheFactory.get(SettingCacheKey(setting));

        } else {
            String storedVal = SettingsDao.getSetting(setting);
            cacheFactory.put(SettingCacheKey(setting), storedVal);
            return storedVal;

        }


    }


    protected static HashMap<String, String> getSettings()  {

       // if (cacheFactory.contains(MemCacheKey.allSettings)) {
         //   return (HashMap<SettingType, String>) cacheFactory.get(MemCacheKey.allSettings);

       // } else {
           final HashMap<String, String> settings = SettingsDao.getSettings();
         //   cacheFactory.put(MemCacheKey.allSettings, settings);
            return settings;
       // }

    }


    protected static void addSetting(final String setting, final String value)  {
        SettingsDao.addSetting(setting, value);


        if (cacheFactory.contains(MemCacheKey.allSettings)) {
            cacheFactory.delete(MemCacheKey.allSettings);
        }
        if (cacheFactory.contains(SettingCacheKey(setting))) {
            cacheFactory.delete(SettingCacheKey(setting));
        }


    }


    protected static void updateSetting(final String setting, final String newValue)  {
        SettingsDao.updateSetting(setting, newValue);


        if (cacheFactory.contains(MemCacheKey.allSettings)) {
            cacheFactory.delete(MemCacheKey.allSettings);
        }
        if (cacheFactory.contains(SettingCacheKey(setting))) {
            cacheFactory.delete(SettingCacheKey(setting));
        }

    }


}
