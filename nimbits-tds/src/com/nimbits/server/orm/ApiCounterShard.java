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

package com.nimbits.server.orm;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/19/11
 * Time: 4:59 PM
 */

import javax.jdo.annotations.*;

/**
 * One shard belonging to the named counter.
 *
 * An individual shard is written to infrequently to allow the counter in
 * aggregate to be incremented rapidly.
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ApiCounterShard {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Integer shardNumber;

    @Persistent
    private String counterName;

    @Persistent
    private Integer count;


    public ApiCounterShard(String counterName, int shardNumber) {
        this(counterName, shardNumber, 0);
    }

    public ApiCounterShard(String counterName, int shardNumber,
                           int count) {
        this.counterName = counterName;
        this.shardNumber = shardNumber;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public String getCounterName() {
        return counterName;
    }

    public Integer getShardNumber() {
        return shardNumber;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void increment(int amount) {
        count = count + amount;
    }

}
