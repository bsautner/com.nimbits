package com.nimbits.io.http.rest;

import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
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
    User getMe();

    @POST(API)
    User addUser(@Body User newUser);

    @POST(API + "/{uuid}")
    Entity addEntity(@Path("uuid") String parent,  @Body Point point);

    @POST(API + "/{uuid}")
    Point addPoint(@Path("uuid") String parent,  @Body Point point);

    @POST(API + "/{uuid}")
    Category addCategory(@Path("uuid") String parent,  @Body Category category);

    @POST(API + "/{uuid}/series")
    void recordData(@Path("uuid") String uuid,  @Body List<Value> values, Callback<Void> callback);

    @GET(API + "/{uuid}/series")
    List<Value> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end, @Query("mask") String mask);

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

    @GET(API + "/{uuid}/children")
    List<Entity> getChildren(@Path("uuid") String uuid);

    @GET(API + "/{uuid}")
    Entity getEntity(@Path("uuid") String uuid);

    @GET(API + "/{uuid}/nearby")
    List<Entity> getNearbyPoints(@Path("uuid")String uuid, @Query("meters") double meters);
}
