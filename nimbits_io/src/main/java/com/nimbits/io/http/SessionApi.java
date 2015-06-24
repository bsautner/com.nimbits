package com.nimbits.io.http;

import com.nimbits.client.model.user.User;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SessionApi {

    final String SESSION_API = "/service/v2/session";

    @POST(SESSION_API)
    User login(@Query("email") String email, @Query("password") String key);

    @POST(SESSION_API)
    User login();

    @GET(SESSION_API)
    User getSession();


}
