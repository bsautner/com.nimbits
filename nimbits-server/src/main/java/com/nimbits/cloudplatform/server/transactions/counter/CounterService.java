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

package com.nimbits.cloudplatform.server.transactions.counter;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 2:37 PM
 */

@Service("counterService")

public class CounterService {


   private static ShardedDate shardedDate;

    static {
        shardedDate = new ShardedDate();
    }
    
   public static void createShards(final String name) {

       final ShardedCounter counter = new ShardedCounter(name);
       counter.addShards(10);

   }

   
   public static void incrementCounter(final String name) {



       final ShardedCounter counter = new ShardedCounter(name);

       counter.increment();

   }

   
   public static long getCount(String name) {
       final ShardedCounter counter = new ShardedCounter(name);
       return counter.getCount();
   }

   
   public static Date updateDateCounter(final String name) {
       shardedDate.setName(name);
       return shardedDate.update();


   }

   
   public static Date getDateCounter(String name) {
       shardedDate.setName(name);
       return shardedDate.getMostRecent();

   }


}
