package com.nimbits.io.http.rest;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import retrofit.http.*;

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
}
