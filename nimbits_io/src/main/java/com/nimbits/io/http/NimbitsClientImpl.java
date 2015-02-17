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


import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.android.AndroidControlFactory;
import com.nimbits.client.android.AndroidControlImpl;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.apikey.AccessCode;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.io.NimbitsClient;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.gson.ValueDeserializer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class NimbitsClientImpl implements NimbitsClient {





    private static final UrlContainer VALUE_SERVICE = UrlContainer.getInstance("/service/v2/value");

    private static final UrlContainer SERIES_SERVICE = UrlContainer.getInstance("/service/v2/series");

    private static final UrlContainer ENTITY_SERVICE = UrlContainer.getInstance("/service/v2/entity");
    private static final UrlContainer HB_SERVICE = UrlContainer.getInstance("/service/v2/hb");
    public static final String HTTP_NIMBITS_GCM_APPSPOT_COM_ANDROID = "http://nimbits-gcm.appspot.com/android";

    public static final int MAX_COUNT = 1000;
    private final HttpHelper helper;
    public final static Type valueListType = new TypeToken<List<ValueModel>>() {
    }.getType();
    public final static Type entityListType = new TypeToken<List<EntityModel>>() {
    }.getType();
    private final EmailAddress email;
    private final UrlContainer instanceUrl;
    private final AccessCode accessCode;
    private final Server server;

    public NimbitsClientImpl(Server server) {
        this.instanceUrl = UrlContainer.getInstance("http://" + server.getUrl());
        this.email = server.getEmail();
        this.helper = new HttpHelper(email, server);
        this.accessCode = server.getAccessCode();
        this.server = server;
    }


    @Override
    public User login() {

        final Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new SessionDeserializer()).create();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                    request.addQueryParam(Parameters.key.getText(), server.getAccessCode().getValue());
                    request.addQueryParam(Parameters.password.getText(), server.getAccessCode().getValue());
                }
            }
        };
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

        return api.login(email.getValue());
    }


    @Override
    public User getSession() {

        final Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new SessionDeserializer()).create();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                    request.addQueryParam(Parameters.key.getText(), server.getAccessCode().getValue());
                    request.addQueryParam(Parameters.email.getText(), server.getEmail().getValue());
                    request.addQueryParam(Parameters.password.getText(), server.getAccessCode().getValue());
                    //TODO collapse all jauth params into one both client and server side
                }
            }
        };
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

        return api.getSession( );
    }


    @Override
    public List<Value> getValue(final Entity entity) {
        UrlContainer path = UrlContainer.combine(instanceUrl, VALUE_SERVICE);

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        return helper.doGet(ValueModel.class, path, params, valueListType, false);

    }

    @Override
    public Map<String, Integer> moveCron() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)

                .build();

        MoveCron cron = restAdapter.create(MoveCron.class);

        return cron.move();


    }

    @Override
    public List<Entity> getTree() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(GsonFactory.getInstance()))
                .build();

        TreeApi api = restAdapter.create(TreeApi.class);

        return api.getTree(email.getValue(), accessCode.getValue());


    }

    @Override
    public List<Value> postValue(final Entity entity, final Value value) {
        UrlContainer path = UrlContainer.combine(instanceUrl, VALUE_SERVICE);

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
        String content = new GsonBuilder().create().toJson(value);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.json.getText(), content)));
        params.add((new BasicNameValuePair(Parameters.email.getText(), email.getValue())));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        return helper.doPost(ValueModel.class, path, params, null, false);


    }

    @Override
    public List<Value> getSeries(final String entity) {



        final Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializer()).create();


        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        List<Value> sample = seriesApi.getSeries(email.getValue(), accessCode.getValue(), entity);

        List<Value> fixed = new ArrayList<>(sample.size());
        Set<Long> test = new HashSet<>(sample.size());
        for (Value value : sample) {
            if (! test.contains(value.getTimestamp().getTime())) {
                fixed.add(value);
                test.add(value.getTimestamp().getTime());
            }

        }
        return sample;

    }


    @Override
    public List<Value> getSeries(final String entity, final int count) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializer()).create();


        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };
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

        List<Value> sample = seriesApi.getSeries(email.getValue(), accessCode.getValue(), entity, count);

        List<Value> fixed = new ArrayList<>(sample.size());
        Set<Long> test = new HashSet<>(sample.size());
        for (Value value : sample) {
            if (! test.contains(value.getTimestamp().getTime())) {
                fixed.add(value);
                test.add(value.getTimestamp().getTime());
            }

        }
        return sample;

    }
    @Override
    public List<Value> getSeries(final String entity, final Range<Date> range) {


        final Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializer()).create();


        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        List<Value> sample = seriesApi.getSeries(email.getValue(), accessCode.getValue(), entity, range.lowerEndpoint().getTime(), range.upperEndpoint().getTime());

        List<Value> fixed = new ArrayList<>(sample.size());
        Set<Long> test = new HashSet<>(sample.size());
        for (Value value : sample) {
            if (! test.contains(value.getTimestamp().getTime())) {
                fixed.add(value);
                test.add(value.getTimestamp().getTime());
            }

        }
        return sample;

    }
    @Override
    public void deleteEntity(final Entity entity) {

        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add((new BasicNameValuePair(Parameters.id.getText(), entity.getKey())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), entity.getEntityType().toString())));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.delete.getCode())));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        helper.doPost(EntityModel.class, path, params, entityListType, false);

    }

    @Override
    public <T, K> List<T> addEntity(Entity entity, final Class<K> clz) {
        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        String json = GsonFactory.getInstance().toJson(entity);
        params.add((new BasicNameValuePair(Parameters.json.getText(), json)));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.create.getCode())));
        params.add((new BasicNameValuePair(Parameters.email.getText(), email.getValue())));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        return helper.doPost(clz, path, params, entityListType, false);

    }


    @Override
    public <T> List<T> updateEntity(Entity entity, Class<T> clz) {
        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        String json = GsonFactory.getInstance().toJson(entity);
        params.add((new BasicNameValuePair(Parameters.json.getText(), json)));
        params.add((new BasicNameValuePair(Parameters.action.getText(), Action.update.getCode())));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        return helper.doPost(clz, path, params, entityListType,true);


    }

    @Override
    public <T, K> List<T> getEntity(final SimpleValue<String> entityId, final EntityType type, final Class<K> clz) {
        UrlContainer path = UrlContainer.combine(instanceUrl, ENTITY_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
        params.add(new BasicNameValuePair(Parameters.id.getText(), entityId.getValue()));
        params.add(new BasicNameValuePair(Parameters.type.getText(), String.valueOf(type.getCode())));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        return helper.doGet(clz, path, params, EntityModel.class, false);


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
    public void doHeartbeat(Entity parent) {
        UrlContainer path = UrlContainer.combine(instanceUrl, HB_SERVICE);
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add((new BasicNameValuePair(Parameters.id.getText(), parent.getKey())));
        params.add((new BasicNameValuePair(Parameters.type.getText(), String.valueOf(parent.getEntityType().getCode()))));
        if (accessCode != null) {
            params.add(new BasicNameValuePair(Parameters.key.name(), accessCode.getValue()));
        }
        helper.doPost(String.class, path, params, entityListType, false);
    }

    @Override
    public void recordSeries(final Point point) {
        recordSeries( Arrays.asList(point), email.getValue());
    }

    @Override
    public void recordSeries(List<Point> points) {
        recordSeries(points, email.getValue());
    }

    @Override
    public void notifySocketConnection(String forwardUrl, User user) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(forwardUrl)
                .setRequestInterceptor(requestInterceptor)

                .build();


        SocketApi socketApi = restAdapter.create(SocketApi.class);
        socketApi.notifyConnection(user);



    }


    private void recordSeries(final List<Point> point, String email) {



        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (!server.getAccessCode().isEmpty()) {
                    request.addHeader(Parameters.apikey.getText(), server.getAccessCode().getValue());
                }
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instanceUrl.getUrl())
                .setRequestInterceptor(requestInterceptor)

                .build();

        SeriesApi seriesApi = restAdapter.create(SeriesApi.class);

        seriesApi.recordSeries(point, email, accessCode.getValue());




    }



}

