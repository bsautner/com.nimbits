package com.nimbits.io.helper;

import com.nimbits.client.model.point.Point;

/**
 * Helper for working with the Entity API REST Services on a Nimbits Server
 * Creates, Reads, Updates and deletes data points.
 *
 * @see com.nimbits.client.model.point.Point
 */
@Deprecated //all helper classes are being replaced with the Nimbits.Builder() client
public interface PointHelper extends EntityHelper {

    /**
     * @param name        the name of the data point, must be unique on the user's account
     * @param description describes the purpose of a point
     * @return the newly created data point.
     */
    Point createPoint(String name, String description);

    /**
     * @param name the name of the data point
     * @return true if point is located.
     */
    boolean pointExists(String name);

    /**
     * @param name the name of the data point throws an exception if the point is not found
     * @return the point if found.
     * @throws java.lang.IllegalArgumentException
     */
    Point getPoint(String name);


}
