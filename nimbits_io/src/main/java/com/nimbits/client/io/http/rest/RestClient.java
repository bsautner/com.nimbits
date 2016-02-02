package com.nimbits.client.io.http.rest;

import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;

public interface RestClient {

    String API = "/service/v3/rest";

    @POST(API)
    User login(@Query("email") String email, @Query("password") String key);

    @POST(API)
    User login();

    @GET(API + "/me")
    User getMe(@Query("children") boolean includeChildren);

    @POST(API)
    User addUser(@Body User newUser);

    @POST(API + "/{uuid}") @Deprecated //use specific adder
    Entity addEntity(@Path("uuid") String parent,  @Body Point point);

    @POST(API + "/{uuid}")
    Point addPoint(@Path("uuid") String parent,  @Body Point point);

    @POST(API + "/{uuid}")
    Connection addConnection(@Path("uuid") String parent,  @Body Connection point);


    @POST(API + "/{uuid}")
    Instance addInstance(@Path("uuid") String parent,  @Body Instance e);


    @POST(API + "/{uuid}")
    Schedule addSchedule(@Path("uuid") String parent,  @Body Schedule e);


    @POST(API + "/{uuid}")
    Category addCategory(@Path("uuid") String parent,  @Body Category category);

    @POST(API + "/{uuid}/series")
    void recordData(@Path("uuid") String uuid,  @Body List<Value> values, Callback<Void> callback);

    @GET(API + "/{uuid}/series")
    List<Value> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end, @Query("mask") String mask);

    @GET(API + "/{uuid}/series")
    List<Value> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end,
                        @Query("mask") String mask, @Query("count") int count);




    @GET(API + "/{uuid}/series")
    List<Value> getData(@Path("uuid") String uuid, @Query("count") Integer count);



    @GET(API + "/{uuid}/series")
    List<Value> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end);


    @POST(API + "/{uuid}/snapshot")
    void updateSnapshot(@Path("uuid") String uuid,  @Body Value values, Callback<Void> callback);

    @GET(API + "/{uuid}/snapshot")
    ValueContainer getSnapshot(@Path("uuid") String uuid);

    @GET(API + "/{uuid}")
    Point getPoint(@Path("uuid") String uuid);

    @POST(API + "/{uuid}")
    WebHook addWebhook(@Path("uuid") String parent,  @Body WebHook webHook);

    @POST(API + "/{uuid}")
    Subscription addSubscription(@Path("uuid") String parent,  @Body Subscription subscription);


    @POST(API + "/{uuid}")
    Sync addSync(@Path("uuid") String parent,  @Body Sync subscription);

    @POST(API + "/{uuid}")
    Calculation addCalc(@Path("uuid") String parent,  @Body Calculation calculation);

    @POST(API + "/{uuid}")
    Summary addSummary(@Path("uuid") String parent,  @Body Summary summary);

    @POST(API + "/{uuid}")
    AccessKey addAccessKey(@Path("uuid") String parent,  @Body AccessKey summary);


    @GET(API + "/{uuid}/children")
    List<Entity> getChildren(@Path("uuid") String uuid);

    @GET(API + "/{uuid}")
    Entity getEntity(@Path("uuid") String uuid);

    @GET(API + "/{uuid}/nearby")
    List<Point> getNearbyPoints(@Path("uuid")String uuid, @Query("meters") double meters);

    @GET(API + "/me")
    Point findPoint(@Query("point") String pointName);



    @DELETE(API + "/{uuid}")
    void deleteEntity(@Path("uuid") String uuid, Callback<Void> callback);

    @GET(API + "/me")
    Entity findEntity(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Category findCategory(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    WebHook findWebHook(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Subscription findSubscription(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Sync findSync(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Calculation findCalculation(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Instance findInstance(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Connection findConnection(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Schedule findSchedule(@Query("name") String entityName, @Query("type") int entityType);


    @GET(API + "/me")
    User findUser(@Query("name") String entityName, @Query("type") int entityType);

    @GET(API + "/me")
    Socket findSocket(@Query("name") String entityName, @Query("type") int entityType);

    @POST(API + "/file/{uuid}")
    void uploadFile(@Path("uuid") String uuid, @Body String encoded, Callback<Void> callback);

    @GET(API + "/file/{uuid}")
    String getFile(@Path("uuid") String uuid);

    @DELETE(API + "/file/{uuid}")
    void deleteFile(@Path("uuid") String uuid, Callback<Void> callback);

    @PUT(API + "/file/{uuid}")
    void updateFile(@Path("uuid") String uuid, @Body String encoded, Callback<Void> callback);


    @PUT(API + "/{uuid}")
    void updateEntity(@Path("uuid")String uuid, @Body Entity entity, Callback<Void> callback);
}
