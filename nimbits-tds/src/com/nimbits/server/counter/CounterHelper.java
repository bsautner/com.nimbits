package com.nimbits.server.counter;

import com.nimbits.server.dao.counter.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 4:19 PM
 */
public interface CounterHelper {
    ShardedCounter getOrCreateCounter(final String name);
}
