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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.android.AndroidControlFactory;
import com.nimbits.client.android.AndroidControlImpl;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.NimbitsClient;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.gson.deserializer.SessionDeserializer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

import java.io.IOException;
import java.util.*;

public class NimbitsClientImpl implements NimbitsClient {


    public static final String HTTP_NIMBITS_GCM_APPSPOT_COM_ANDROID = "http://nimbits-gcm.appspot.com/android";
    private final UrlContainer instanceUrl;
    private final Server server;
    private final RequestInterceptor requestInterceptor;


    public NimbitsClientImpl(final Server theServer) {
        this.instanceUrl = UrlContainer.getInstance("http://" + theServer.getUrl());
        this.server = theServer;

        this.requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessToken().isEmpty()) {
                    request.addHeader(Parameters.token.getText(), theServer.getAccessToken().getValue());

                }
                request.addQueryParam(Parameters.email.getText(), theServer.getEmail().getValue());

            }
        };

    }


    @Override
    public User login() {

        final Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new SessionDeserializer()).create();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        throw new NimbitsClientException(retrofitError.getMessage());
                    }
                })
                .build();

        SessionApi api = restAdapter.create(SessionApi.class);

        return api.login();
    }


    @Override
    public User getSession() {

        final Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new SessionDeserializer()).create();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        throw new NimbitsClientException(retrofitError.getMessage());
                    }
                })
                .build();

        SessionApi api = restAdapter.create(SessionApi.class);

        return api.getSession();
    }


    @Override
    public Value getValue(final String entityName) {
        final Gson gson = new GsonBuilder().create();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        throw new NimbitsClientException(retrofitError.getMessage());
                    }
                })
                .build();

        ValueApi api = restAdapter.create(ValueApi.class);
        String fixed;
        if (!entityName.startsWith(server.getEmail().getValue())) {
            fixed = server.getEmail().getValue() + "/" + entityName;
        } else {
            fixed = entityName;
        }
        return api.getValue(fixed);

    }

    @Override
    public Map<String, Integer> moveCron() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)

                .build();

        SystemCron cron = restAdapter.create(SystemCron.class);

        return cron.execute();


    }

    @Override
    public List<Entity> getTree() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(GsonFactory.getInstance()))
                .build();

        TreeApi api = restAdapter.create(TreeApi.class);

        return ImmutableList.copyOf(api.getTree());


    }

    @Override
    public void postValue(final Entity entity, final Value value) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)

                .build();

        ValueApi valueApi = restAdapter.create(ValueApi.class);
        valueApi.postValue(value, entity.getKey());


    }

    @Override
    public List<Value> getSeries(final String entity) {


        final Gson gson = new GsonBuilder().create();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        List<Value> sample = seriesApi.getSeries(entity);

        List<Value> fixed = new ArrayList<Value>(sample.size());
        Set<Long> test = new HashSet<Long>(sample.size());
        for (Value value : sample) {
            if (!test.contains(value.getTimestamp().getTime())) {
                fixed.add(value);
                test.add(value.getTimestamp().getTime());
            }

        }
        return ImmutableList.copyOf(sample);

    }


    @Override
    public List<Value> getSeries(final String entity, final int count) {
        final Gson gson = new GsonBuilder().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        throw new NimbitsClientException(retrofitError.getMessage());
                    }
                })
                .setConverter(new GsonConverter(gson))
                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        List<Value> sample = seriesApi.getSeries(entity, count);

        List<Value> fixed = new ArrayList<Value>(sample.size());
        Set<Long> test = new HashSet<Long>(sample.size());
        for (Value value : sample) {
            if (!test.contains(value.getTimestamp().getTime())) {
                fixed.add(value);
                test.add(value.getTimestamp().getTime());
            }

        }
        return ImmutableList.copyOf(sample);


    }

    @Override
    public List<Value> getSeries(final String entity, final Range<Date> range) {


        final Gson gson = new GsonBuilder().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        List<Value> sample = seriesApi.getSeries(entity, range.lowerEndpoint().getTime(), range.upperEndpoint().getTime());

        List<Value> fixed = new ArrayList<Value>(sample.size());
        Set<Long> test = new HashSet<Long>(sample.size());
        for (Value value : sample) {
            if (!test.contains(value.getTimestamp().getTime())) {
                fixed.add(value);
                test.add(value.getTimestamp().getTime());
            }

        }
        return ImmutableList.copyOf(sample);

    }

    @Override
    public String deleteEntity(final Entity entity) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(entity.getEntityType().getClz(), SerializationHelper.getDeserializer(entity.getEntityType()))
                .excludeFieldsWithoutExposeAnnotation().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();
        EntityApi api = restAdapter.create(EntityApi.class);
        return api.deleteEntity(entity, entity.getKey(), Action.delete.name(), entity.getEntityType().name());

    }

    @Override
    public Entity addEntity(Entity entity) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(entity.getEntityType().getClz(),
                        SerializationHelper.getDeserializer(entity.getEntityType()))
                .excludeFieldsWithoutExposeAnnotation().create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();

        EntityApi api = restAdapter.create(EntityApi.class);

        switch (entity.getEntityType()) {

            case user:
                return api.addEntity(entity);
            case point:
                return api.addPoint(entity);
            case category:
                return api.addCategory(entity);
            case subscription:
                return api.addSubscription(entity);
            case sync:
                return api.addSync(entity);
            case calculation:
                return api.addCalculation(entity);
            case summary:
                return api.addSummaryentity(entity);
            case accessKey:
                return api.addAccessToken(entity);
            case instance:
                return api.addInstance(entity);
            case socket:
                return api.addSocket(entity);
            case connection:
                return api.addConnection(entity);
            case schedule:
                return api.addSchedule(entity);
            case webhook:
                return api.addWebHook(entity);
            default:
                return api.addEntity(entity);
        }


    }


    @Override
    public <T> List<T> updateEntity(Entity entity, Class<T> clz) {
        return null;


    }

    @Override
    public Entity getEntity(final SimpleValue<String> entityId, final EntityType entityType) {


        final Gson g = new GsonBuilder()
                .registerTypeAdapter(entityType.getClz(), SerializationHelper.getDeserializer(entityType))

                .create();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(g))
                .build();

        EntityApi entityApi = adapter.create(EntityApi.class);
        Entity result = null;

        switch (entityType) {

            case user:
                result = entityApi.getUser(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case point:

                result = entityApi.getPoint(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case category:

                result = entityApi.getCategory(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case subscription:

                result = entityApi.getSubscription(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case sync:

                result = entityApi.getSync(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case calculation:

                result = entityApi.getCalc(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case summary:

                result = entityApi.getSummary(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case accessKey:

                result = entityApi.getToken(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case instance:
                result = entityApi.getInstance(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case socket:

                result = entityApi.getSocket(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case connection:

                result = entityApi.getConnection(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case schedule:

                result = entityApi.getSchedule(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
            case webhook:

                result = entityApi.getWebHook(server.getEmail().getValue(), server.getAccessToken().getValue(), entityId.getValue());

                break;
        }


        return result;


    }


    @Override
    public List<AndroidControl> getControl() {
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        String getURL = HTTP_NIMBITS_GCM_APPSPOT_COM_ANDROID;
        HttpGet get = new HttpGet(getURL);
        HttpResponse responseGet;
        List<AndroidControl> result = new ArrayList<AndroidControl>(1);

        try {
            responseGet = client.execute(get);

            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {

                String response = EntityUtils.toString(resEntityGet);
                if (!Utils.isEmptyString(response)) {
                    Gson gson = new GsonBuilder().create();
                    AndroidControl c = gson.fromJson(response, AndroidControlImpl.class);
                    if (c != null) {
                        result.add(c);
                    }
                }


            }
        } catch (IOException e) {
            result.add(AndroidControlFactory.getConservativeInstance());

        } catch (JsonSyntaxException e) {
            result.add(AndroidControlFactory.getConservativeInstance());
        }
        return result;


    }

    @Override
    public void recordSeries(final Point point) {
        recordSeries(Arrays.asList(point));
    }


    @Override
    public void notifySocketConnection(String forwardUrl, User user) {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(forwardUrl)
                .setRequestInterceptor(requestInterceptor)

                .build();


        SocketApi socketApi = restAdapter.create(SocketApi.class);
        socketApi.notifyConnection(user);


    }


    @Override

    public void recordSeries(final List<Point> point) {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)

                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        seriesApi.recordSeries(point);


    }


}

