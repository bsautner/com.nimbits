package com.nimbits.server.counter;

import com.nimbits.server.dao.counter.*;

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
