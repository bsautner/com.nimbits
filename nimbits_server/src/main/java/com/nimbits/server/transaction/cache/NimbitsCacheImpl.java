/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;


public class NimbitsCacheImpl extends BaseCache implements NimbitsCache {
    private Cache<Object, Object> cache;

    public NimbitsCacheImpl() {
        cache = CacheBuilder.newBuilder().build();

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
    public void delete(String key) {
        remove(generateKey(key));
    }


}
