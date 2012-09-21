
package com.nimbits.server.transactions.dao.counter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Random;


public class ShardedCounter {


    private static final class Counter {

        private static final String KIND = "Counter";
        private static final String SHARD_COUNT = "shard_count";
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
        private static final String KIND_PREFIX = "CounterShard_";

        /**
         * Property to store the current count within a counter shard.
         */
        private static final String COUNT = "count";
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
    private final String counterName;

    /**
     * A random number generating, for distributing writes across shards.
     */
    private final Random generator = new Random();

    /**
     * The counter shard kind for this counter.
     */
    private String kind;

    private final MemcacheService mc = MemcacheServiceFactory
            .getMemcacheService();

    /**
     * Constructor which creates a sharded counter using the provided counter
     * name.
     *
     * @param counterName name of the sharded counter
     */
    public ShardedCounter(String counterName) {
        this.counterName = counterName;
        kind = CounterShard.KIND_PREFIX + counterName;
    }

    /**
     * Increase the number of shards for a given sharded counter. Will never
     * decrease the number of shards.
     *
     * @param count Number of new shards to build and store
     */
    public void addShards(int count) {
        Key counterKey = KeyFactory.createKey(Counter.KIND, counterName);
        incrementPropertyTx(counterKey, Counter.SHARD_COUNT, count, INITIAL_SHARDS
                + count);
    }

    /**
     * Retrieve the value of this sharded counter.
     *
     * @return Summed total of all shards' counts
     */
    public long getCount() {
        Long value = (Long) mc.get(kind);
        if (value != null) {
            return value;
        }

        long sum = 0;
        Query query = new Query(kind);
        for (Entity shard : ds.prepare(query).asIterable()) {
            sum += (Long) shard.getProperty(CounterShard.COUNT);
        }
        mc.put(kind, sum, Expiration.byDeltaSeconds(60),
                SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return sum;
    }

    /**
     * Increment the value of this sharded counter.
     */
    public void increment() {
        // Find how many shards are in this counter.
        int numShards = getShardCount();

        // Choose the shard randomly from the available shards.
        long shardNum = generator.nextInt(numShards);

        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        incrementPropertyTx(shardKey, CounterShard.COUNT, 1, 1);
        mc.increment(kind, 1);
    }

    /**
     * Get the number of shards in this counter.
     *
     * @return shard count
     */
    private int getShardCount() {
        try {
            Key counterKey = KeyFactory.createKey(Counter.KIND, counterName);
            Entity counter = ds.get(counterKey);
            Long shardCount = (Long) counter.getProperty(Counter.SHARD_COUNT);
            return shardCount.intValue();
        } catch (EntityNotFoundException ignore) {
            return INITIAL_SHARDS;
        }
    }

    /**
     * Increment datastore property value inside a transaction. If the entity with
     * the provided key does not exist, instead create an entity with the supplied
     * initial property value.
     *
     * @param key the entity key to update or create
     * @param prop the property name to be incremented
     * @param increment the amount by which to increment
     * @param initialValue the value to use if the entity does not exist
     */
    private void incrementPropertyTx(Key key, String prop, long increment,
                                     long initialValue) {
        Transaction tx = ds.beginTransaction();
        Entity thing;
        long value;
        try {
            thing = ds.get(tx, key);
            value = (Long) thing.getProperty(prop) + increment;
        } catch (EntityNotFoundException e) {
            thing = new Entity(key);
            value = initialValue;
        }
        thing.setUnindexedProperty(prop, value);
        ds.put(tx, thing);
        tx.commit();
    }
}