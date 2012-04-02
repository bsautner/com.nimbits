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

package com.nimbits.server.transactions.dao.counter;

import com.nimbits.PMF;
import com.nimbits.server.orm.ApiCounter;
import com.nimbits.server.orm.ApiCounterShard;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * A counter which can be incremented rapidly.
 *
 * Capable of incrementing the counter and increasing the number of shards.
 * When incrementing, a random shard is selected to prevent a single shard
 * from being written to too frequently. If increments are being made too
 * quickly, increase the number of shards to divide the load. Performs
 * datastore operations using JDO.
 *
 * Lookups are attempted using Memcache (Jcache). If the counter value is
 * not in the cache, the shards are read from the datastore and accumulated
 * to reconstruct the current count.
 *
 */
@SuppressWarnings("unchecked")
public class ShardedCounter {
    private static final Logger log = Logger.getLogger(ShardedCounter.class.getName());

    private String counterName;
    private Cache cache;
    private static final String COUNT = "count";
    private static final String SHARDS = "shards";


    public ShardedCounter(final String counterName) {
        this.counterName = counterName;
        cache = null;
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(
                    Collections.emptyMap());
        } catch (CacheException e) {
            log.severe(e.getMessage());
        }
    }

    private ApiCounter getThisCounter(final PersistenceManager pm) {
        ApiCounter current = null;
        final Query thisCounterQuery = pm.newQuery(ApiCounter.class,
                "counterName == nameParam");
        thisCounterQuery.declareParameters("String nameParam");
        final List<ApiCounter> counter =
                (List<ApiCounter>) thisCounterQuery.execute(counterName);
        if (counter != null && !counter.isEmpty()) {
            current = counter.get(0);
        }
        return current;
    }

    public boolean isInDatastore() {
        boolean counterStored = false;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            if (getThisCounter(pm) != null) {
                counterStored = true;
            }
        } finally {
            pm.close();
        }
        return counterStored;
    }

    public int getCount() {
        if (cache != null) {
            final Integer cachedCount = (Integer) cache.get(COUNT + counterName);
            if (cachedCount != null) {
                return cachedCount;
            }
        }

        int sum = 0;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query shardsQuery = pm.newQuery(ApiCounterShard.class);
            shardsQuery.setFilter("counterName == nameParam");
            shardsQuery.declareParameters("String nameParam");
            final List<ApiCounterShard> shards =
                    (List<ApiCounterShard>) shardsQuery.execute(counterName);
            if (shards != null && !shards.isEmpty()) {
                for (final ApiCounterShard current : shards) {
                    sum += current.getCount();
                }
            }
        } finally {
            pm.close();
        }

        if (cache != null) {
            cache.put(COUNT + counterName, sum);
        }

        return sum;
    }
//
//    public int getNumShards() {
//        if (cache != null) {
//            final Integer cachedCount = (Integer) cache.get(SHARDS + counterName);
//            if (cachedCount != null) {
//                return cachedCount;
//            }
//        }
//
//        int numShards = 0;
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        try {
//            final ApiCounter current = getThisCounter(pm);
//            if (current != null) {
//                numShards = current.getShardCount();
//            }
//        } finally {
//            pm.close();
//        }
//
//        if (cache != null) {
//            cache.put(SHARDS + counterName, numShards);
//        }
//
//        return numShards;
//    }

    public int addShard() {
        return addShards(1);
    }

    public int addShards(final int totalCount) {
        int numShards = 0;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final ApiCounter current = getThisCounter(pm);
            if (current != null) {
                numShards = current.getShardCount();
                current.setShardCount(numShards + totalCount);
                pm.makePersistent(current);
            }
        } finally {
            pm.close();
        }

        pm = PMF.get().getPersistenceManager();
        try {
            ApiCounterShard newShard;
            for (int i = 0; i < totalCount; i++) {

                newShard = new ApiCounterShard(
                        counterName, numShards);
                pm.makePersistent(newShard);
                numShards++;
            }
        } finally {
            pm.close();
        }

        if (cache != null) {
            cache.put(SHARDS + counterName, numShards);
        }

        return numShards;
    }

    public void increment() {
        increment(1);
    }

    public void increment(final int totalCount) {
        if (cache != null) {
            final Integer cachedCount = (Integer) cache.get(COUNT + counterName);
            if (cachedCount != null) {
                cache.put(COUNT + counterName,
                        totalCount + cachedCount);
            }
        }

        int shardCount = 0;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final ApiCounter current = getThisCounter(pm);
            shardCount = current.getShardCount();
        } finally {
            pm.close();
        }

        final Random generator = new Random();
        final int shardNum = generator.nextInt(shardCount);

        pm = PMF.get().getPersistenceManager();
        try {
            final Query randomShardQuery = pm.newQuery(ApiCounterShard.class);
            randomShardQuery.setFilter(
                    "counterName == nameParam && shardNumber == numParam");
            randomShardQuery.declareParameters("String nameParam, int numParam");
            final List<ApiCounterShard> shards =
                    (List<ApiCounterShard>) randomShardQuery.execute(
                            counterName, shardNum);
            if (shards != null && !shards.isEmpty()) {
                final ApiCounterShard shard = shards.get(0);
                shard.increment(totalCount);
                pm.makePersistent(shard);
            }
        } finally {
            pm.close();
        }
    }
}