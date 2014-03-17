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

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 2:37 PM
 */

public class CounterServiceImpl implements CounterService {


   private ShardedDate shardedDate;

    public CounterServiceImpl() {
        this.shardedDate = new ShardedDate();
    }

    
   @Override
   public void createShards(final String name) {

       final ShardedCounter counter = new ShardedCounter(name);
       counter.addShards(10);

   }

   
   @Override
   public void incrementCounter(final String name) {



       final ShardedCounter counter = new ShardedCounter(name);

       counter.increment();

   }

   
   @Override
   public long getCount(String name) {
       final ShardedCounter counter = new ShardedCounter(name);
       return counter.getCount();
   }

   
   @Override
   public Date updateDateCounter(final String name) {
       shardedDate.setName(name);
       return shardedDate.update();


   }

   
   @Override
   public Date getDateCounter(String name) {
       shardedDate.setName(name);
       return shardedDate.getMostRecent();

   }


}
