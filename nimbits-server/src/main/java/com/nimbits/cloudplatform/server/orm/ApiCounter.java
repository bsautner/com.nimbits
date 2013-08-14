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

package com.nimbits.cloudplatform.server.orm;




/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/19/11
 * Time: 4:58 PM
 */

import javax.jdo.annotations.*;

/**
 * Represents a counter in the datastore and stores the number of shards.
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ApiCounter {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String counterName;

    @Persistent
    private Integer numShards;

    public ApiCounter(final String counterName) {
        this.counterName = counterName;
        this.numShards = 0;
    }


    public ApiCounter(final String counterName, final Integer numShards) {
        this.counterName = counterName;
        this.numShards = numShards;
    }

    public Long getId() {
        return id;
    }

    public String getCounterName() {
        return counterName;
    }

    public Integer getShardCount() {
        return numShards;
    }

    public void setShardCount(final int count) {
        this.numShards = count;
    }


}