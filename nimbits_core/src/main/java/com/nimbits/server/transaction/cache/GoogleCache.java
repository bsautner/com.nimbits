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

package com.nimbits.server.transaction.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nimbits.client.enums.MemCacheKey;


public class GoogleCache extends BaseCache implements NimbitsCache {
    Cache<Object, Object> cache;

    public GoogleCache() {
        cache = CacheBuilder.newBuilder().build();
    }

    @Override
    public boolean containsKey(String key) {
        return cache.asMap().containsKey(generateKey(key));
    }

    @Override
    public void remove(String key) {
        cache.invalidate(generateKey(key));
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(generateKey(key));
    }

    @Override
    public void put(String key, Object object) {
        cache.put(generateKey(key), object);
    }

    @Override
    public void reloadCache() {
         cache.invalidateAll();
    }

    @Override
    public boolean confirmCached(String key) {
        return cache.asMap().containsKey(generateKey(key));
    }

    @Override
    public boolean contains(MemCacheKey key) {
        return containsKey(key.getText());
    }

    @Override
    public Object get(MemCacheKey key) {
        return get(key.getText());
    }

    @Override
    public void delete(MemCacheKey key) {
         remove(key.getText());
    }

    @Override
    public void put(MemCacheKey key, Object newMap) {
          put(key.getText(), newMap);
    }

    @Override
    public boolean contains(String key) {
        return containsKey(generateKey(key));
    }

    @Override
    public void delete(String key) {
        remove(generateKey(key));
    }
}
