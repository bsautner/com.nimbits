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

package com.nimbits.server.memcache.settings;

import com.nimbits.client.common.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.server.settings.*;
import net.sf.jsr107cache.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 10:44 AM
 */
public class SettingMemCacheImpl implements SettingTransactions {
    Cache cache;
    private final static String ALL_SETTING_CACHE_KEY = Const.CACHE_KEY_PREFIX + "ALL_SETTING_CACHE_KEY";

    private String SettingCacheKey(final String paramName) {
        return Const.CACHE_KEY_PREFIX + "SETTING" + SettingMemCacheImpl.class.getName() + paramName;
    }

    @Override
    public String reloadCache() throws NimbitsException {

        final StringBuilder builder = new StringBuilder();
        builder.append("<h5>Removing old values from memcache</h5>");

        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            final Map<String, String> settings = SettingTransactionsFactory.getDaoInstance().getSettings();
            for (final String setting : settings.keySet()) {
                cache.remove(ALL_SETTING_CACHE_KEY);
                builder.append("Removed: ").append(setting).append("<br />");
                cache.remove(SettingCacheKey(setting));


            }


        } catch (CacheException e) {
            builder.append(e.getMessage());
        }
        return builder.toString();

    }


    @Override
    public String getSetting(final String paramName) throws NimbitsException {

        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            if (cache.containsKey(SettingCacheKey(paramName))) {
                return (String) cache.get(SettingCacheKey(paramName));

            } else {
                String storedVal = SettingTransactionsFactory.getDaoInstance().getSetting(paramName);
                if (!Utils.isEmptyString(storedVal)) {
                    cache.put(SettingCacheKey(paramName), storedVal);
                    return storedVal;
                } else {
                    throw new NimbitsException("Setting Not Found");
                }
            }

        } catch (CacheException e) {
            throw new NimbitsException(e.getMessage());
        }

    }

    @Override
    public String getServerSecret() throws NimbitsException {
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            if (cache.containsKey(SettingCacheKey(Const.Params.PARAM_SECRET))) {
                return (String) cache.get(SettingCacheKey(Const.Params.PARAM_SECRET));

            } else {
                String storedVal = SettingTransactionsFactory.getDaoInstance().getServerSecret();
                if (!Utils.isEmptyString(storedVal)) {
                    cache.put(SettingCacheKey(Const.Params.PARAM_SECRET), storedVal);
                    return storedVal;
                } else {
                    throw new NimbitsException("Server Secret not found " + UUID.randomUUID().toString());
                }
            }

        } catch (CacheException e) {
            throw new NimbitsException(e.getMessage());
        }

    }

    @Override
    public Map<String, String> getSettings() throws NimbitsException {
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            if (cache.containsKey(ALL_SETTING_CACHE_KEY)) {
                return (Map<String, String>) cache.get(ALL_SETTING_CACHE_KEY);

            } else {
                Map<String, String> settings = SettingTransactionsFactory.getDaoInstance().getSettings();
                cache.put(ALL_SETTING_CACHE_KEY, settings);
                return settings;
            }
        } catch (CacheException e) {
            throw new NimbitsException(e.getMessage());
        }
    }

    @Override
    public void addSetting(final String name, final String value) {
        SettingTransactionsFactory.getDaoInstance().addSetting(name, value);
        if (cache.containsKey(ALL_SETTING_CACHE_KEY)) {
            cache.remove(ALL_SETTING_CACHE_KEY);
        }
        if (cache.containsKey(SettingCacheKey(name))) {
            cache.remove(SettingCacheKey(name));
        }

    }

    @Override
    public void updateSetting(final String name, final String newValue) {
        SettingTransactionsFactory.getDaoInstance().updateSetting(name, newValue);
        if (cache.containsKey(ALL_SETTING_CACHE_KEY)) {
            cache.remove(ALL_SETTING_CACHE_KEY);
        }
        if (cache.containsKey(SettingCacheKey(name))) {
            cache.remove(SettingCacheKey(name));
        }
    }


}
