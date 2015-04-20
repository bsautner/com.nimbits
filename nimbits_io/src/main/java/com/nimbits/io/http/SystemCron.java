package com.nimbits.io.http;

import retrofit.http.GET;

import java.util.Map;

public interface SystemCron {

    final String CRON ="/cron/system";


    @GET(CRON)
    Map<String, Integer> execute();
}
