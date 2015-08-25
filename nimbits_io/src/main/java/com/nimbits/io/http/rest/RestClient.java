package com.nimbits.io.http.rest;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
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
    Entity addEntity(@Path("uuid") String uuid,  @Body Point point);

    @POST(API + "/{uuid}/series")
    void recordData(@Path("uuid") String uuid,  @Body List<Value> values, Callback<Void> callback);

    @GET(API + "/{uuid}/series")
    List<Value> getData(@Path("uuid") String uuid, @Query("start") long start, @Query("end") long end);

    @POST(API + "/{uuid}/snapshot")
    void updateSnapshot(@Path("uuid") String uuid,  @Body Value values, Callback<Void> callback);

    @GET(API + "/{uuid}/snapshot")
    List<Value> getSnapshot(@Path("uuid") String uuid);
}
