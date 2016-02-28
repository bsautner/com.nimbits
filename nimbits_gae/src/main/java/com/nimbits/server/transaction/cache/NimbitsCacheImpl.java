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

import com.nimbits.client.constants.Const;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import java.util.Collections;
import java.util.logging.Logger;


public class NimbitsCacheImpl extends BaseCache implements NimbitsCache {
    final static Logger logger = Logger.getLogger(NimbitsCacheImpl.class.getName());

    private Cache cache;

    private final static String GLOBAL_CACHE = "GLOBAL_CACHE" + Const.VERSION + "_";


    public NimbitsCacheImpl() throws CacheException {


        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        cache = CacheManager.getInstance().getCache(GLOBAL_CACHE);
        if (cache == null) {
            cache = cacheFactory.createCache(Collections.emptyMap());
            CacheManager.getInstance().registerCache(GLOBAL_CACHE, cache);

        }


    }


    @Override
    @Deprecated
    public void remove(String key) {
        cache.remove(generateKey(key));
    }

    @Override
    @Deprecated
    public Object get(String key) {
        return cache.get(generateKey(key));
    }

    @Override
    @Deprecated
    public void put(String key, Object object) {
        cache.put(generateKey(key), object);
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
    }


}
