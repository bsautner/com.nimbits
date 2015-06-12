/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

package com.nimbits.io.http;


import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import java.util.List;

public interface SeriesApi {

    String SERIES_API ="/service/v2/series";

    @POST(SERIES_API)
    String recordSeries(@Body final List<Point> point);

    @GET(SERIES_API)
    List<Value>  getSeries(@Query("id") String id);

    @GET(SERIES_API)
    List<Value>  getSeries(@Query("id") String id, @Query("count") int count);

    @GET(SERIES_API)
    List<Value>  getSeries(@Query("id") String id,
                           @Query("count") long sd,
                           @Query("ed") long ed);
}
