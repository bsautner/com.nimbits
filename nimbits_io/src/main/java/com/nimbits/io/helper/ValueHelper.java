package com.nimbits.io.helper;

import com.google.common.collect.Range;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Helper for working with the Value API REST Services on a Nimbits Server
 * Records new values and downloads series of values
 *
 * @see com.nimbits.client.model.value.Value
 */
@Deprecated //all helper classes are being replaced with the Nimbits.Builder() client
public interface ValueHelper {

    /**
     * @param pointName the name of the data point
     * @param value     any double value
     * @return the newly recorded value pojo with a timestamp of the current time the value was recorded.
     */
    void recordValue(String pointName, double value);

    /**
     * @param pointName the name of the data point
     * @param value     any a Value Model Object @see Value implements Value
     * @return the newly recorded value pojo with a timestamp of the current time the value was recorded.
     */
    void recordValue(String pointName, Value value);

    /**
     * @param name the name of the data point
     * @return a list of value objects
     */
    List<Value> getSeries(String name);

    /**
     * @param name  the name of the data point
     * @param value any double value
     * @param time  the timestamp for this point
     * @return the recorded value
     */
    void recordValue(String name, double value, Date time);


    List<Value> getSeries(String name, Range<Date> dateRange);

    /**
     * @param name  the name of the data point
     * @param count the number of values to return, starting from the most recent in time and going back.
     * @return a list of values up to the count.  May be less than the count if that many values don't exist.
     */
    List<Value> getSeries(String name, int count);

    /**
     * @param pointName the name of the data point
     * @param data      a list of Value Objects @see Value
     */
    void recordValues(String pointName, List<Value> data);

    /**
     * @param points a list of points with the values field set to the list of values to record.
     */
    void recordValues(List<Point> points);

    /**
     * execute the execute cron task.
     */
    Map<String, Integer> moveCron();

    Value getValue(String name);
}
