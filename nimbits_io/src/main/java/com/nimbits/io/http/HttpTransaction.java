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

package com.nimbits.io.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.apikey.ApiKey;
import com.nimbits.server.gson.GsonFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

@Deprecated
public class HttpTransaction {

    private static final int INT_OK = 200;
    private static final String PARAM_DELIM = "?";
    private static final String PARAM_ACSID = "ACSID";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String CONST_ENCODING = "UTF-8";

    private final EmailAddress email;
    private final Server server;

    public HttpTransaction(EmailAddress email, Server server) {
        this.email = email;
        this.server = server;
    }

    private Gson gson = GsonFactory.getInstance();


    private <T> List<T> stringToList(final Type listType, String result) {

        return gson.fromJson(result, listType);

    }

    private <T, K> T stringToObject(Class<K> clz, String result) throws Exception {
        try {
            K entity = gson.fromJson(result, clz);

            return (T) entity;
        }
        catch (JsonSyntaxException ex) {
            throw new Exception(ex);
        }

    }

    public <T, K> List<T> doGet(final Class<K> clz,
                                final UrlContainer postUrl,
                                final List<BasicNameValuePair> params,
                                final Type listType,
                                final boolean expectList) {


        String result = null;



        params.add(new BasicNameValuePair(Parameters.email.getText(), email.getValue()));


        String paramString = URLEncodedUtils.format(params, CONST_ENCODING);
        String path = postUrl.getUrl() + PARAM_DELIM + paramString;
        HttpGet http = new HttpGet(path);

        try {

            http.addHeader(ACCEPT, APPLICATION_JSON);
            http.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            if (server.getApiKey() != null && ! server.getApiKey().isEmpty()) {

                http.addHeader(Parameters.apikey.getText(),server.getApiKey().getValue());
            }
            HttpResponse response = getClient(server.getApiKey()).execute(http);
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

        }


        return processResponse(clz, listType, result, expectList);

    }

    public <T, K> List<T> doPost(final Class<K> clz,
                                 final UrlContainer postUrl,
                                 final List<BasicNameValuePair> parameters,
                                 final Type type,
                                 final boolean expectList) {
        String result = null;
        HttpPost http = new HttpPost(postUrl.getUrl());
        try {
            addParameters(parameters, http);
            if (! server.getApiKey().isEmpty()) {
                http.addHeader(Parameters.apikey.getText(),server.getApiKey().getValue());
            }
            HttpResponse response = getClient(server.getApiKey()).execute(http);

            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            result = (reader.readLine());
            inputStream.close();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processResponse(clz, type, result, expectList);

    }

    private void addParameters(List<BasicNameValuePair> parameters, HttpPost httppost) throws UnsupportedEncodingException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parameters.size());
        for (BasicNameValuePair value : parameters) {
            nameValuePairs.add(value);
        }
        nameValuePairs.add(new BasicNameValuePair(Parameters.email.getText(), email.getValue()));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    }

    private  <T, K> List<T> processResponse(Class<K> clz,
                                            Type listType,
                                            String result,
                                            boolean expectList) {
        if (StringUtils.isEmpty(result)) {
            return Collections.emptyList();
        } else {
            if (expectList) {
                return stringToList(listType, result);
            } else {
                try {
                    return (List<T>) Arrays.asList(stringToObject(clz, result));
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            }
        }
    }


    private DefaultHttpClient getClient(final ApiKey apiKey) {

            HttpParams headerParams = new BasicHttpParams();
            headerParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            headerParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            if (apiKey != null && ! apiKey.isEmpty()) {
                headerParams.setParameter(ServerSetting.apiKey.getName(), apiKey);
            }
            int timeoutConnection = 15000;
            HttpConnectionParams.setConnectionTimeout(headerParams, timeoutConnection);

            int timeoutSocket = 15000;
            HttpConnectionParams.setSoTimeout(headerParams, timeoutSocket);

            return new DefaultHttpClient(headerParams);


        }


}
