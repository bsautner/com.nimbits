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

package com.nimbits.server.transactions.counter;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Date;
import java.util.Random;

public class ShardedDate {


    private MemcacheService cacheFactory;

    private class Counter {

        private static final String KIND = "LastDateShard";
        private static final String SHARD_COUNT = "shard_date";
    }

    /**
     * Convenience class which contains constants related to the counter shards.
     * The shard number (as a String) is used as the entity key.
     */
    private static final class CounterShard {
        /**
         * Entity kind prefix, which is concatenated with the counter name to form
         * the final entity kind, which represents counter shards.
         */
        private static final String KIND_PREFIX = "DateShard_";

        /**
         * Property to store the current count within a counter shard.
         */
        private static final String TIME = "time";
    }

    private static final DatastoreService ds = DatastoreServiceFactory
            .getDatastoreService();

    /**
     * Default number of shards.
     */
    private static final int INITIAL_SHARDS = 5;

    /**
     * The name of this counter.
     */
    private String name;

    /**
     * A random number generating, for distributing writes across shards.
     */
    private final Random generator = new Random();

    /**
     * The counter shard kind for this counter.
     */
    private String kind;

    public void setName(String name) {
        this.name = name;
        kind = CounterShard.KIND_PREFIX + name;
    }


    public ShardedDate() {
        cacheFactory = MemcacheServiceFactory.getMemcacheService();
    }


    /**
     * Increase the number of shards for a given sharded counter. Will never
     * decrease the number of shards.
     *
     * @param count Number of new shards to build and store
     */
    public void addShards(int count) {
        Key counterKey = KeyFactory.createKey(Counter.KIND, name);
        incrementPropertyTx(counterKey, Counter.SHARD_COUNT, new Date());
    }

    /**
     * Retrieve the value of this sharded counter.
     *
     * @return Summed total of all shards' counts
     */
    public Date getMostRecent() {
        Long value = (Long) cacheFactory.get(kind);
        if (value != null) {
            return new Date(value);
        }

        Date retVal = new Date(0);
        Query query = new Query(kind);
        for (Entity shard : ds.prepare(query).asIterable()) {
            Date option = new Date((Long) shard.getProperty(CounterShard.TIME));
            if (option.getTime() > retVal.getTime()) {
                retVal = option;
            }
        }
        cacheFactory.put(kind, retVal.getTime(), Expiration.byDeltaSeconds(60),
                SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return retVal;
    }

    /**
     * Increment the value of this sharded counter.
     */
    public Date update() {
        // Find how many shards are in this counter.
        int numShards = getShardCount();

        // Choose the shard randomly from the available shards.
        long shardNum = generator.nextInt(numShards);

        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        Date d = new Date();
        incrementPropertyTx(shardKey, CounterShard.TIME, new Date());
        cacheFactory.increment(kind, 1);
        return d;
    }

    /**
     * Get the number of shards in this counter.
     *
     * @return shard count
     */
    private int getShardCount() {
        try {
            Key counterKey = KeyFactory.createKey(Counter.KIND, name);
            Entity counter = ds.get(counterKey);
            Long shardCount = (Long) counter.getProperty(Counter.SHARD_COUNT);
            return shardCount.intValue();
        } catch (EntityNotFoundException ignore) {
            return INITIAL_SHARDS;
        }
    }

    private void incrementPropertyTx(Key key, String prop, Date newDate) {
        Transaction tx = ds.beginTransaction();
        Entity thing;
        long value;
        try {
            thing = ds.get(tx, key);
            value = newDate.getTime();
        } catch (EntityNotFoundException e) {
            thing = new Entity(key);
            value = new Date().getTime();
        }
        thing.setUnindexedProperty(prop, value);
        ds.put(tx, thing);
        tx.commit();
    }
}