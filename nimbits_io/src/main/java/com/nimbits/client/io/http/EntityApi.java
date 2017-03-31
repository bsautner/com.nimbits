/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.io.http;


import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EntityApi {

    String API = "/service/v2/entity";


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
    Instance getInstance(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Schedule getSchedule(@Query("email") String email, @Query("key") String key, @Query("id") String id);

    @GET(API)
    Schedule getWebHook(@Query("email") String email, @Query("key") String key, @Query("id") String id);


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
    Instance addInstance(@Body Entity entity);


    @POST(API)
    Schedule addSchedule(@Body Entity entity);

    @POST(API)
    Schedule addWebHook(@Body Entity entity);

    @POST(API)
    String deleteEntity(@Body Entity entity, @Query("id") String id, @Query("action") String action, @Query("type") String type);
}
