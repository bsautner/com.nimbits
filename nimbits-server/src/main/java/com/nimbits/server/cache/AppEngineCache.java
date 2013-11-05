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

package com.nimbits.server.cache;

import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.server.transaction.cache.BaseCache;
import com.nimbits.server.transaction.cache.NimbitsCache;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import java.util.Collections;

public class AppEngineCache extends BaseCache implements NimbitsCache {
    private final Cache cache;

    public AppEngineCache() throws CacheException {

        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        cache = cacheFactory.createCache(Collections.emptyMap());

    }

    @Override
    public boolean containsKey(String key) {
        try {
            return cache.containsKey(generateKey(key));
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public void remove(String key) {
        cache.remove(generateKey(key));
    }

    @Override
    public Object get(String key) {
        return cache.get(generateKey(key));
    }

    @Override
    public void put(String key, Object object) {
        cache.put(generateKey(key), object);
    }


    @Override
    public void reloadCache() {
        cache.clear();

    }

    @Override
    public boolean confirmCached(String key) {

        return cache.containsKey(generateKey(key));
    }

    @Override
    public boolean contains(MemCacheKey key) {
        return containsKey(generateKey(key.getText()));
    }

    @Override
    public Object get(MemCacheKey key) {
        return this.get(generateKey(key.getText()));
    }

    @Override
    public void delete(MemCacheKey key) {
        this.remove(generateKey(key.getText()));
    }

    @Override
    public void put(MemCacheKey key, Object newMap) {
        this.put(generateKey(key.getText()), newMap);
    }

    @Override
    public boolean contains(String key) {
        return containsKey(generateKey(key));
    }

    @Override
    public void delete(String key) {
        cache.remove(generateKey(key));
    }


}
