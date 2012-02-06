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

package com.nimbits.server.dao.counter;

import com.nimbits.*;
import com.nimbits.server.orm.*;
import net.sf.jsr107cache.*;

import javax.jdo.*;
import java.util.*;

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
public class ShardedCounter {
    private String counterName;
    private Cache cache;
    private final String COUNT = "count";
    private final String SHARDS = "shards";
    public ShardedCounter(String counterName) {
        this.counterName = counterName;
        cache = null;
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(
                    Collections.emptyMap());
        } catch (CacheException e) {
        }
    }

    public String getCounterName() {
        return counterName;
    }

    private DatastoreCounter getThisCounter(PersistenceManager pm) {
        DatastoreCounter current = null;
        Query thisCounterQuery = pm.newQuery(DatastoreCounter.class,
                "counterName == nameParam");
        thisCounterQuery.declareParameters("String nameParam");
        List<DatastoreCounter> counter =
                (List<DatastoreCounter>) thisCounterQuery.execute(counterName);
        if (counter != null && !counter.isEmpty()) {
            current = counter.get(0);
        }
        return current;
    }

    public boolean isInDatastore() {
        boolean counterStored = false;
        PersistenceManager pm = PMF.get().getPersistenceManager();
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
            Integer cachedCount = (Integer) cache.get(COUNT + counterName);
            if (cachedCount != null) {
                return cachedCount.intValue();
            }
        }

        int sum = 0;
        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Query shardsQuery = pm.newQuery(DatastoreCounterShard.class,
                    "counterName == nameParam");
            shardsQuery.declareParameters("String nameParam");
            List<DatastoreCounterShard> shards =
                    (List<DatastoreCounterShard>) shardsQuery.execute(counterName);
            if (shards != null && !shards.isEmpty()) {
                for (DatastoreCounterShard current : shards) {
                    sum += current.getCount();
                }
            }
        } finally {
            pm.close();
        }

        if (cache != null) {
            cache.put(COUNT + counterName, Integer.valueOf(sum));
        }

        return sum;
    }

    public int getNumShards() {
        if (cache != null) {
            Integer cachedCount = (Integer) cache.get(SHARDS + counterName);
            if (cachedCount != null) {
                return cachedCount.intValue();
            }
        }

        int numShards = 0;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            DatastoreCounter current = getThisCounter(pm);
            if (current != null) {
                numShards = current.getShardCount().intValue();
            }
        } finally {
            pm.close();
        }

        if (cache != null) {
            cache.put(SHARDS + counterName, Integer.valueOf(numShards));
        }

        return numShards;
    }

    public int addShard() {
        return addShards(1);
    }

    public int addShards(int totalCount) {
        int numShards = 0;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            DatastoreCounter current = getThisCounter(pm);
            if (current != null) {
                numShards = current.getShardCount().intValue();
                current.setShardCount(numShards + totalCount);
                pm.makePersistent(current);
            }
        } finally {
            pm.close();
        }

        pm = PMF.get().getPersistenceManager();
        try {
            for (int i = 0; i < totalCount; i++) {
                DatastoreCounterShard newShard = new DatastoreCounterShard(
                        getCounterName(), numShards);
                pm.makePersistent(newShard);
                numShards++;
            }
        } finally {
            pm.close();
        }

        if (cache != null) {
            cache.put(SHARDS + counterName, Integer.valueOf(numShards));
        }

        return numShards;
    }

    public void increment() {
        increment(1);
    }

    public void increment(final int totalCount) {
        if (cache != null) {
            Integer cachedCount = (Integer) cache.get(COUNT + counterName);
            if (cachedCount != null) {
                cache.put(COUNT + counterName,
                        Integer.valueOf(totalCount + cachedCount.intValue()));
            }
        }

        int shardCount = 0;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            DatastoreCounter current = getThisCounter(pm);
            shardCount = current.getShardCount();
        } finally {
            pm.close();
        }

        Random generator = new Random();
        int shardNum = generator.nextInt(shardCount);

        pm = PMF.get().getPersistenceManager();
        try {
            Query randomShardQuery = pm.newQuery(DatastoreCounterShard.class);
            randomShardQuery.setFilter(
                    "counterName == nameParam && shardNumber == numParam");
            randomShardQuery.declareParameters("String nameParam, int numParam");
            List<DatastoreCounterShard> shards =
                    (List<DatastoreCounterShard>) randomShardQuery.execute(
                            counterName, shardNum);
            if (shards != null && !shards.isEmpty()) {
                DatastoreCounterShard shard = shards.get(0);
                shard.increment(totalCount);
                pm.makePersistent(shard);
            }
        } finally {
            pm.close();
        }
    }
}