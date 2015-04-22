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
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableSet;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.server.api.UsageTracker;
import org.apache.http.annotation.Immutable;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@Component
public class NimbitsCacheImpl extends BaseCache implements NimbitsCache {
    private Cache<Object, Object> cache;
    private Cache<String, UsageTracker> usageCache;
    private Cache<String, String> activeCache;

    private final Logger logger = Logger.getLogger(NimbitsCacheImpl.class.getName());

    public NimbitsCacheImpl() {
        cache = CacheBuilder.newBuilder().build();
        usageCache = CacheBuilder.newBuilder()
                .removalListener(new RemovalListener<String, UsageTracker>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, UsageTracker> removalNotification) {
                        logger.severe("removing " + removalNotification.getValue().toGson());
                    }
                })

                .build();


        activeCache = CacheBuilder.newBuilder().build();


    }


    @Override
    public ConcurrentMap<String, UsageTracker> getUsageMap() {
        return usageCache.asMap();
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
    public void putUsage(String key, UsageTracker object) {
        usageCache.put(generateKey(key), object);
    }

    @Override
    public UsageTracker getUsage(String key) {
        return usageCache.getIfPresent(generateKey(key));
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

    @Override
    public Set getActivePoints() {
        return ImmutableSet.of(activeCache.asMap().values());
    }

    @Override
    public void flushActivePoints() {
        activeCache.invalidateAll();
    }

    @Override
    public long getStats() {
        return 0;
    }
}
