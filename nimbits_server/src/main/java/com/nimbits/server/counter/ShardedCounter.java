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

package com.nimbits.server.counter;

import java.util.Random;


public class ShardedCounter {


    private class Counter {

        private static final String KIND = "Counter";
        private static final String SHARD_COUNT = "shard_count";
    }

    /**
     * Convenience class which contains constants related to the counter shards.
     * The shard number (as a String) is used as the entity key.
     */
    private final class CounterShard {
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

    }

    /**
     * Retrieve the value of this sharded counter.
     *
     * @return Summed total of all shards' counts
     */
    public long getCount() {
        return 0;
    }

    /**
     * Increment the value of this sharded counter.
     */
    public void increment() {
        // Find how many shards are in this counter.

    }

    /**
     * Get the number of shards in this counter.
     *
     * @return shard count
     */
    private int getShardCount() {
        return 0;
    }

    /**
     * Increment datastore property value inside a transaction. If the entity with
     * the provided key does not exist, instead create an entity with the supplied
     * initial property value.
     *
     * @param key          the entity key to update or create
     * @param prop         the property name to be incremented
     * @param increment    the amount by which to increment
     * @param initialValue the value to use if the entity does not exist
     */
    private void incrementPropertyTx(String key, String prop, long increment,
                                     long initialValue) {

    }
}