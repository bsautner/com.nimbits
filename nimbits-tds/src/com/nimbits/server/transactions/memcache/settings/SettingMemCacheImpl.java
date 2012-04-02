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

package com.nimbits.server.transactions.memcache.settings;

import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.settings.SettingTransactions;
import com.nimbits.server.settings.SettingTransactionsFactory;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import java.util.Collections;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 10:44 AM
 */
@SuppressWarnings("unchecked")
public class SettingMemCacheImpl implements SettingTransactions {
    Cache cache;

    private String SettingCacheKey(final SettingType setting) {
        return MemCacheKey.setting + setting.getName();
    }

    @Override
    public String reloadCache() throws NimbitsException {

        final StringBuilder builder = new StringBuilder(1024);
        builder.append("<h5>Removing old values from memcache</h5>");

        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            final Map<SettingType, String> settings = SettingTransactionsFactory.getDaoInstance().getSettings();
            for (final SettingType setting : settings.keySet()) {
                cache.remove(MemCacheKey.allSettings);
                if (setting != null) {
                builder.append("Removed: ").append(setting.getName()).append("<br />");
                cache.remove(SettingCacheKey(setting));
                }



            }


        } catch (CacheException e) {
            builder.append(e.getMessage());
        }
        return builder.toString();

    }


    @Override
    public String getSetting(final SettingType setting) throws NimbitsException {

        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            if (cache.containsKey(SettingCacheKey(setting))) {
                return (String) cache.get(SettingCacheKey(setting));

            } else {
                String storedVal = SettingTransactionsFactory.getDaoInstance().getSetting(setting);
                cache.put(SettingCacheKey(setting), storedVal);
                return storedVal;

            }

        } catch (CacheException e) {
            throw new NimbitsException(e.getMessage());
        }

    }

    @Override
    public Map<SettingType, String> getSettings() throws NimbitsException {
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            if (cache.containsKey(MemCacheKey.allSettings)) {
                return (Map<SettingType, String>) cache.get(MemCacheKey.allSettings);

            } else {
                Map<SettingType, String> settings = SettingTransactionsFactory.getDaoInstance().getSettings();
                cache.put(MemCacheKey.allSettings, settings);
                return settings;
            }
        } catch (CacheException e) {
            throw new NimbitsException(e.getMessage());
        }
    }

    @Override
    public void addSetting(final SettingType setting, final String value) {
        SettingTransactionsFactory.getDaoInstance().addSetting(setting, value);
        if (cache.containsKey(MemCacheKey.allSettings)) {
            cache.remove(MemCacheKey.allSettings);
        }
        if (cache.containsKey(SettingCacheKey(setting))) {
            cache.remove(SettingCacheKey(setting));
        }

    }

    @Override
    public void updateSetting(final SettingType setting, final String newValue) {
        SettingTransactionsFactory.getDaoInstance().updateSetting(setting, newValue);
        if (cache.containsKey(MemCacheKey.allSettings)) {
            cache.remove(MemCacheKey.allSettings);
        }
        if (cache.containsKey(SettingCacheKey(setting))) {
            cache.remove(SettingCacheKey(setting));
        }
    }


}
