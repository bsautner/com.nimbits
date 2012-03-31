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

package com.nimbits.server.counter;

import com.nimbits.server.dao.counter.ShardedCounter;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 4:20 PM
 */
public class CounterHelperImpl implements CounterHelper{

    public ShardedCounter getOrCreateCounter(String s) {
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getCounter(s);
        if (counter == null) {
            counter = factory.createCounter(s);
            counter.addShard();

        }
        return counter;
    }
}
