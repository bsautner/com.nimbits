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
import com.google.common.collect.ImmutableList;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.UsageTracker;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.value.dao.ValueDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class NimbitsCacheImpl extends BaseCache implements NimbitsCache {
    private Cache<Object, Object> cache;
    private Cache<String, UsageTracker> usageCache;
    private Cache<String, List<Value>> valueCache;

    @Autowired
    private ValueDao valueDao;

    @Autowired
    private EntityDao entityDao;


    private final Logger logger = Logger.getLogger(NimbitsCacheImpl.class.getName());

    public NimbitsCacheImpl() {
        cache = CacheBuilder.newBuilder().build();
        usageCache = CacheBuilder.newBuilder().build();


        valueCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, List<Value>>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, List<Value>> removalNotification) {
                        Point point = entityDao.getPoint(removalNotification.getKey());
                        try {
                            logger.info("moving values to store: " + removalNotification.getValue().size());
                            valueDao.recordValues(point, removalNotification.getValue());
                        } catch (IOException e) {
                            logger.severe("error on value eviction" + e.getMessage());
                        }
                    }
                }).build();




    }


    @Override
    public List<Value> getValueBuffer(Entity entity) {
        return
                valueCache.getIfPresent(entity.getKey()) == null
                        ? Collections.<Value>emptyList()
                        : ImmutableList.copyOf(valueCache.getIfPresent(entity.getKey()));
    }

    @Override
    public void bufferValue(Entity entity, Value value) {
        if (valueCache.getIfPresent(entity.getKey()) != null) {
            valueCache.getIfPresent(entity.getKey()).add(value);
        }
        else {
            List<Value> l = new ArrayList<>();
            l.add(value);
            valueCache.put(entity.getKey(), l);
        }
    }

    @Override
    public ConcurrentMap<String, UsageTracker> getUsageMap() {
        return usageCache.asMap();
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
    public void delete(String key) {
        remove(generateKey(key));
    }

    @Override
    public long getStats() {
        return 0;
    }


}
