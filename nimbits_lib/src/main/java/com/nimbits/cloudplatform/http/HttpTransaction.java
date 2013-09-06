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

package com.nimbits.cloudplatform.http;

import com.google.gson.Gson;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * Author: Benjamin Sautner
 * Date: 1/18/13
 * Time: 9:28 AM
 */
public class HttpTransaction {

    private static final int INT_OK = 200;
    private static final String PARAM_DELIM = "?";
    private static final String PARAM_ACSID = "ACSID";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String CONST_ENCODING = "UTF-8";


    private static Gson gson;

    public static void init( Gson aGson) {


        gson = aGson;


    }

    private static <T> List<T> stringToList(final Type listType, String result) {

        return gson.fromJson(result, listType);

    }

    private static <T, K> T stringToObject(Class<K> clz, String result) {

        K entity = gson.fromJson(result, clz);

        return (T) entity;

    }

    public static <T, K> List<T> doGet(final Class<K> clz,
                                       final UrlContainer postUrl,
                                       final List<BasicNameValuePair> parameter,
                                       final Type listType,
                                       final boolean expectList) {


        String result = null;

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        for (BasicNameValuePair v : parameter) {
            params.add(v);

        }
        if (! StringUtils.isEmpty(Nimbits.email)) {

            params.add(new BasicNameValuePair(Parameters.email.getText(), Nimbits.email));
        }

        String paramString = URLEncodedUtils.format(params, CONST_ENCODING);
        String path = postUrl.getUrl() + PARAM_DELIM + paramString;
        HttpGet http = new HttpGet(path);

        try {
            http.addHeader(ACCEPT, APPLICATION_JSON);
            http.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            if (Nimbits.getApiKey() != null) {

                http.addHeader(Parameters.apikey.getText(),Nimbits.getApiKey());
            }
            HttpResponse response = HttpClientFactory.getInstance().execute(http);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == INT_OK) {

                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    result = (reader.readLine());
                    inputStream.close();
                }
            }

        } catch (Exception e) {

            return Collections.emptyList();

        } finally {
          //  http.abort();
          ///  HttpClientFactory.getInstance().getConnectionManager().closeExpiredConnections();
        }


        return processResponse(clz, listType, result, expectList);

    }

    public static <T, K> List<T> doPost(final Class<K> clz,
                                        final UrlContainer postUrl,
                                        final List<BasicNameValuePair> parameters,
                                        final Type Type,
                                        final boolean expectList) {
        String result = null;
        HttpPost http = new HttpPost(postUrl.getUrl());
        try {
            addParameters(parameters, http);
            if (Nimbits.getApiKey() != null) {
                http.addHeader(Parameters.apikey.getText(),Nimbits.getApiKey());
            }
            HttpResponse response = HttpClientFactory.getInstance().execute(http);

            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            result = (reader.readLine());
            inputStream.close();

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        } finally {
            http.abort();
            HttpClientFactory.getInstance().getConnectionManager().closeExpiredConnections();
        }
        return processResponse(clz, Type, result, expectList);

    }

    private static void addParameters(List<BasicNameValuePair> parameters, HttpPost httppost) throws UnsupportedEncodingException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
        for (BasicNameValuePair value : parameters) {
            nameValuePairs.add(value);
        }
        if (! StringUtils.isEmpty(Nimbits.email)) {

            nameValuePairs.add(new BasicNameValuePair(Parameters.email.getText(), Nimbits.email));
        }
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    }

    private static <T, K> List<T> processResponse(Class<K> clz,
                                                  Type listType,
                                                  String result,
                                                  boolean expectList) {
        if (StringUtils.isEmpty(result)) {
            return Collections.emptyList();
        } else {
            if (expectList) {
                return stringToList(listType, result);
            } else {
                return (List<T>) Arrays.asList(stringToObject(clz, result));
            }
        }
    }


}
