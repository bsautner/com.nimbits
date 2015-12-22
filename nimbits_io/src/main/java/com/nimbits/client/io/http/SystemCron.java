package com.nimbits.client.io.http;

import retrofit.http.GET;

import java.util.Map;

public interface SystemCron {

    String CRON = "/cron/systemCron";


    @GET(CRON)
    Map<String, Integer> execute();
}
