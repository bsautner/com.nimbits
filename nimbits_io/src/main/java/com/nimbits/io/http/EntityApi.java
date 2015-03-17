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


import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface EntityApi  {

    final String API ="/service/v2/entity";


    @GET(API)
    Point getPoint(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    User getUser(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Category getCategory(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Subscription getSubscription(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Sync getSync(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Calculation getCalc(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Summary getSummary(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    AccessKey getToken(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Instance getInstance(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Socket getSocket(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Connection getConnection(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Schedule getSchedule(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @POST(API)
    Entity addEntity(@Body Entity entity);


    @POST(API)
    Point addPoint(@Body Entity entity);

    @POST(API)
    Category addCategory(@Body Entity entity);

    @POST(API)
    Subscription addSubscription(@Body Entity entity);

    @POST(API)
    Sync addSync(@Body Entity entity);

    @POST(API)
    Calculation addCalculation(@Body Entity entity);

    @POST(API)
    Summary addSummaryentity(@Body Entity entity);

    @POST(API)
    AccessKey addAccessToken(@Body Entity entity);

    @POST(API)
    Instance addInstance(@Body Entity entity);

    @POST(API)
    Socket addSocket(@Body Entity entity);

    @POST(API)
    Connection addConnection(@Body Entity entity);

    @POST(API)
    Schedule addSchedule(@Body Entity entity);
}
