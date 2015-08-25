package com.nimbits.io.http;

import com.nimbits.client.model.value.Value;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ValueApi {

    final String API = "/service/v2/value";

    @POST(API)
    String postValue(@Body Value value, @Query("id") String id);

    @GET(API)
    Value getValue(@Query("id") String name);
}
