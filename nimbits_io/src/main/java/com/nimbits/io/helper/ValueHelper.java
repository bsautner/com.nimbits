package com.nimbits.io.helper;

import com.google.common.collect.Range;
import com.nimbits.client.model.value.Value;

import java.util.Date;
import java.util.List;


/**
 * Helper for working with the Value API REST Services on a Nimbits Server
 * Records new values and downloads series of values
 * @see com.nimbits.client.model.value.Value
 */
public interface ValueHelper {

    /**
     *
     * @param pointName the name of the data point
     * @param value any double value
     * @return the newly recorded value pojo with a timestamp of the current time the value was recorded.
     */
    Value recordValue(String pointName, double value);

    /**
     *
     * @param name the name of the data point
     * @return a list of value objects
     */
    List<Value> getSeries(String name);

    /**
     *
     * @param name the name of the data point
     * @param value any double value
     * @param time the timestamp for this point
     * @return the recorded value
     */
    Value recordValue(String name, double value, Date time);


    List<Value> getSeries(String name, Range<Date> dateRange);

    /**
     *
     * @param name the name of the data point
     * @param count the number of values to return, starting from the most recent in time and going back.
     * @return a list of values up to the count.  May be less than the count if that many values don't exist.
     */
    List<Value> getSeries(String name, int count);
}
