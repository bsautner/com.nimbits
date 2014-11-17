package com.nimbits.io.http;

import retrofit.http.GET;

import java.util.Map;

public interface MoveCron {

    final String CRON ="/cron/moveCron";


    @GET(CRON)
    Map<String, Integer> move();
}
