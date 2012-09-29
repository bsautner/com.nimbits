/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.service.counter;

import com.nimbits.server.transactions.dao.counter.ShardedCounter;
import com.nimbits.server.transactions.dao.counter.ShardedDate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 2:37 PM
 */

@Service("counterService")
public class CounterServiceImpl implements CounterService {


   @Override
   public void createShards(String name) {

       ShardedCounter counter = new ShardedCounter(name);
       counter.addShards(10);

   }

   @Override
   public void incrementCounter(String name) {



       ShardedCounter counter = new ShardedCounter(name);

       counter.increment();

   }

   @Override
   public long getCount(String name) {
       ShardedCounter counter = new ShardedCounter(name);
       return counter.getCount();
   }

   @Override
   public Date updateDateCounter(String name) {
       ShardedDate counter = new ShardedDate(name);
       return counter.update();


   }

   @Override
   public Date getDateCounter(String name) {
       ShardedDate counter = new ShardedDate(name);
       return counter.getMostRecent();

   }


}
