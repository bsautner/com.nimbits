/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.value.impl.ValueModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.*;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 11:31 AM
 */
public class Program {
    public static String postUrl = "http://localhost:8080/service/v2/value";
    public static String cronUrl = "http://localhost:8080/cron/pointCron";
    public static String seriesUrl = "http://localhost:8080/service/v2/series";
    public static int errors;
    public static int runs;
    public static Gson gson = new GsonBuilder().create();
    public static void main(String[] args) throws IOException, InterruptedException {

        int i = 0;
        errors = 0;
        runs = 0;

        while (true) {
            i++;
            runs++;
            System.out.println("Health: " + (100 - ((errors / runs) * 100)) + "%");
            String result;
            HttpPost http = new HttpPost(postUrl);
            try {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>(2);

                Value value = ValueFactory.createValueModel(i, new Date());
                String j = gson.toJson(value);
                parameters.add((new BasicNameValuePair(Parameters.email.getText(), "bsautner@gmail.com")));
                parameters.add((new BasicNameValuePair(Parameters.json.getText(), j)));
                parameters.add((new BasicNameValuePair(Parameters.key.getText(), "key")));
                parameters.add((new BasicNameValuePair(Parameters.id.getText(), "one")));
                addParameters(parameters, http);

                http.addHeader(Parameters.apikey.getText(),"KEY");

                HttpResponse response = HttpClientFactory.getInstance().execute(http);

                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                result = (reader.readLine());
                System.out.println(i + "  " + result);
                inputStream.close();


                if (i >= 60) {
                    try {
                        downloadValues();

                        doPointCron(cronUrl);

                        downloadValues();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        errors++;
                    }
                    i = 0;

                }
                Thread.sleep(100);


            }  catch (SocketException ex) {
                errors++;
                Thread.sleep(5000);


            } finally {

                HttpClientFactory.getInstance().getConnectionManager().closeExpiredConnections();
            }

        }

    }

    protected static void downloadValues() throws Exception {
        List<Value> series = getSeries();
        int i = 60;
        Collections.sort(series);
        for (Value v : series) {
            System.out.println("checking: " + v.getDoubleValue() + "  " + v.getTimestamp() + "::" + v.getTimestamp().getTime());
            if ((int) v.getDoubleValue() != i) {
                throw new Exception("series value was wrong expected " + i + " was " + v.getDoubleValue());
            }
            i--;


        }
        Value v = getValue();
        System.out.println("current value: " + v.getDoubleValue());
        if (Double.compare(v.getDoubleValue(), 60.0) != 0 ) {
            throw new Exception("current value was wrong");

        }
    }

    protected static List<Value> getSeries() throws IOException {
        HttpGet request = new HttpGet(seriesUrl + "?id=one&email=bsautner@gmail.com&count=60");
        HttpClient client = new DefaultHttpClient();

        HttpResponse response = client.execute(request);

// Get the response
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(response.getEntity().getContent()));

        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        final Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();

        List<Value> r = gson.fromJson(sb.toString(), valueListType);
        return r;
    }

    protected static Value getValue() throws IOException {
        HttpGet request = new HttpGet(postUrl + "?id=one&email=bsautner@gmail.com");
        HttpClient client = new DefaultHttpClient();

        HttpResponse response = client.execute(request);

// Get the response
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(response.getEntity().getContent()));

        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        final Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();

        Value r = gson.fromJson(sb.toString(), ValueModel.class);
        return r;
    }


    protected static void doPointCron(String cronUrl) throws IOException, InterruptedException {
        HttpPost cron = new HttpPost(cronUrl);
        HttpResponse cronResponse = HttpClientFactory.getInstance().execute(cron);
        System.out.println("Cron Response: " + cronResponse.getStatusLine().getStatusCode());
        System.out.println("***********************************************");
        Thread.sleep(1000);
        cron.releaseConnection();
    }

    private static void addParameters(List<BasicNameValuePair> parameters, HttpPost httppost) throws UnsupportedEncodingException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
        for (BasicNameValuePair value : parameters) {
            nameValuePairs.add(value);
        }

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    }
    public static void testSummary() {
//        String p1 = "summary_test";
//        Value v = ValueHelper.recordValue(p1, 1.0);
//        System.out.println("Returned: " + v.getDoubleValue());

    }

}
