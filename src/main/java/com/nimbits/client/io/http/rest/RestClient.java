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

package com.nimbits.client.io.http.rest;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.Calculation;
import com.nimbits.client.model.Entity;
import com.nimbits.client.model.Event;
import com.nimbits.client.model.Filter;
import com.nimbits.client.model.Group;
import com.nimbits.client.model.Instance;
import com.nimbits.client.model.Schedule;
import com.nimbits.client.model.Subscription;
import com.nimbits.client.model.Summary;
import com.nimbits.client.model.Sync;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface RestClient {

    String API = "/service/v3/rest";

    @GET(API + "/me")
    Call<User> getMe(@Query("children") boolean includeChildren);

    @POST(API + "/")
    Call<User> addUser(@Body User newUser);

    @POST(API + "/{uuid}")
    Call<Topic> addPoint(@Path("uuid") String parent, @Body Topic topic);

    @POST(API + "/{uuid}")
    Call<Instance> addInstance(@Path("uuid") String parent, @Body Instance e);

    @POST(API + "/{uuid}")
    Call<Filter> addFilter(@Path("uuid") String parent, @Body Filter e);

    @POST(API + "/{uuid}")
    Call<Event> addEvent(@Path("uuid") String parent, @Body Event e);


    @POST(API + "/{uuid}")
    Call<Schedule> addSchedule(@Path("uuid") String parent, @Body Schedule e);


    @POST(API + "/{uuid}")
    Call<Group> addCategory(@Path("uuid") String parent, @Body Group group);

    @POST(API + "/{uuid}/series")
    Call<ResponseBody> recordData(@Path("uuid") String uuid, @Body List<Value> values);

    @POST(API + "/sync/{uuid}/snapshot")
    Call<Value> recordSnapshotSync(@Path("uuid") String uuid, @Body Value value);


    @GET(API + "/{uuid}/series")
    Call<List<Value>> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end, @Query("mask") String mask);

    @GET(API + "/{uuid}/series")
    Call<List<Value>> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end,
                        @Query("mask") String mask, @Query("count") int count);


    @GET(API + "/{uuid}/series")
    Call<List<Value>> getData(@Path("uuid") String uuid, @Query("count") Integer count);


    @GET(API + "/{uuid}/series")
    Call<List<Value>> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end);


    @POST(API + "/{uuid}/snapshot")
    Response updateSnapshot(@Path("uuid") String uuid, @Body Value values);

    @GET(API + "/{uuid}/snapshot")
    Call<ValueContainer> getSnapshot(@Path("uuid") String uuid);

    @GET(API + "/{uuid}")
    Call<Topic> getPoint(@Path("uuid") String uuid);

    @POST(API + "/{uuid}")
    Call<WebHook> addWebhook(@Path("uuid") String parent, @Body WebHook webHook);

    @POST(API + "/{uuid}")
    Call<Subscription> addSubscription(@Path("uuid") String parent, @Body Subscription subscription);


    @POST(API + "/{uuid}")
    Call<Sync> addSync(@Path("uuid") String parent, @Body Sync subscription);


    @POST(API + "/{uuid}")
    Call<Summary> addSummary(@Path("uuid") String parent, @Body Summary summary);

    @GET(API + "/{uuid}/children")
    Call<List<Entity>> getChildren(@Path("uuid") String uuid);

    @GET(API + "/{uuid}")
    Call<Entity> getEntity(@Path("uuid") String uuid);

    @GET(API + "/{type}/{uuid}")
    Call<Entity> getEntity(@Path("type") EntityType type, @Path("uuid") String uuid);


    @GET(API + "/{uuid}/nearby")
    Call<List<Topic>> getNearbyPoints(@Path("uuid") String uuid, @Query("meters") double meters);

    @GET(API + "/search")
    Call<Topic> findTopic(@Query("name") String pointName, @Query("type") int entityType);


    @DELETE(API + "/{uuid}")
    Call<ResponseBody> deleteEntity(@Path("uuid") String uuid);

    @GET(API + "/search")
    Call<Group> findCategory(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<WebHook> findWebHook(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<Subscription> findSubscription(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<Sync> findSync(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<Calculation> findCalculation(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<Instance> findInstance(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<Schedule> findSchedule(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/search")
    Call<User> findUser(@Query("name") String entityName, @Query("type") int entityType);

    @PUT(API + "/{uuid}")
    Call<ResponseBody>  updateEntity(@Path("uuid") String uuid, @Body Entity entity);

    @POST(API + "/{uuid}/snapshot")
    Call<ResponseBody> setSnapshot(@Path("uuid") String id, @Body Value value);

    @GET(API + "/admin/user")
    Call<User> getUser(@Query("email") String email);

    @DELETE(API + "/admin/user")
    Call<User> deleteUser(@Query(value = "email") String email);

    @GET(API + "/search")
    Call<Summary> findSummary(@Query("name") String entityName, @Query("type") int entityType);

    @PUT(API + "/sync/{uuid}")
    Call<Entity> updateEntitySync(@Path("uuid") String uuid, @Body Entity entity);

    @POST(API + "/{uuid}")
    Call<Calculation> addCalc(@Path("uuid") String parent, @Body Calculation calculation);

}
