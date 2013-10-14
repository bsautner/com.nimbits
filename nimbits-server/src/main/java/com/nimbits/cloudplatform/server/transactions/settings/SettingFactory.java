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

import com.nimbits.cloudplatform.client.service.settings.SettingsService;
import com.nimbits.cloudplatform.server.transactions.cache.CacheFactory;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.datastore.NimbitsStore;
import net.sf.jsr107cache.CacheException;

/**
 * Created by benjamin on 10/9/13.
 */
public class SettingFactory {
    public static Settings getCacheInstance(NimbitsStore store) throws CacheException {

        NimbitsCache cache = CacheFactory.getInstance();
        return new SettingsCacheImpl(cache, store);
    }

    public static SettingsService getServiceInstance() {
        return new SettingsServiceImpl();
    }
}
