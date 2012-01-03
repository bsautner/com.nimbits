package com.nimbits.server.counter;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/19/11
 * Time: 5:00 PM
 */

import com.nimbits.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.orm.*;

import javax.jdo.PersistenceManager;

/**
 * Finds or creates a sharded counter with the desired name.
 *
 */
public class CounterFactory {

    public ShardedCounter getCounter(final String name) {
        ShardedCounter counter = new ShardedCounter(name);
        if (counter.isInDatastore()) {
            return counter;
        } else {
            return null;
        }
    }

    public ShardedCounter createCounter(final String name) {
        ShardedCounter counter = new ShardedCounter(name);

        DatastoreCounter counterEntity = new DatastoreCounter(name, 0);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(counterEntity);
        } finally {
            pm.close();
        }

        return counter;
    }
}
