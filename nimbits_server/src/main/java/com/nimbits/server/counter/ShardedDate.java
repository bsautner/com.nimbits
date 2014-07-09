
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


import java.util.Date;
import java.util.Random;

public class ShardedDate {


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
    public Date getMostRecent() {
        return null;
    }

    /**
     * Increment the value of this sharded counter.
     */
    public Date update() {
        // Find how many shards are in this counter.

        return null;
    }

    /**
     * Get the number of shards in this counter.
     *
     * @return shard count
     */
    private int getShardCount() {
        return 0;
    }

    private void incrementPropertyTx(String key, String prop, Date newDate) {

    }
}