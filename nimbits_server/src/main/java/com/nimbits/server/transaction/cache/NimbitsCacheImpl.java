/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

package com.nimbits.server.transaction.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nimbits.client.model.value.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class NimbitsCacheImpl extends BaseCache implements NimbitsCache {
    private Cache<Object, Object> cache;


    private final Logger logger = Logger.getLogger(NimbitsCacheImpl.class.getName());

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
