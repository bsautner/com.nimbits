package com.nimbits.server.transactions.service.counter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 3:16 PM
 */
public interface CounterService {
    void createShards(String name);

    void incrementCounter(String name);

    long getCount(String name);

    Date updateDateCounter(String name);

    Date getDateCounter(String name);
}
