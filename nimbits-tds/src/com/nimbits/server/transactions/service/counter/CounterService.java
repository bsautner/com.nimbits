package com.nimbits.server.transactions.service.counter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 3:16 PM
 */
public interface CounterService {
    void createShards(final String name);

    void incrementCounter(final String name);

    long getCount(final String name);

    Date updateDateCounter(final String name);

    Date getDateCounter(final String name);
}
