/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.io;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.ex.NimbitsClientException;
import com.nimbits.client.io.http.rest.RestClient;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.server.gson.GsonFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simpler java client for interacting with the V3 REST API using hal+json
 * and basic authentication
 */
@SuppressWarnings("unused")
public class Nimbits {


    private final RestClient api;

    protected Nimbits(final String email, final String password, String host) throws KeyManagementException, NoSuchAlgorithmException {


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS);

        boolean allowUntrusted = true;

        if (allowUntrusted) {

            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] cArrr = new X509Certificate[0];
                    return cArrr;
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }
            }};

            SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            clientBuilder.sslSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    return true;
                }
            };
            clientBuilder.hostnameVerifier(hostnameVerifier);
        }


        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                     //   .addHeader("user-agent", "nimbits.io")
                        .addHeader("Authorization", "Basic " + email + ":" + password)
                        .addHeader("Accept", "application/json");

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });


        OkHttpClient client = clientBuilder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(client)

                .addConverterFactory(GsonConverterFactory.create(GsonFactory.getInstance(false)))
                .build();

        api = retrofit.create(RestClient.class);

    }


    public void connect() {

    }

    /**
     * @return the authentication user from /service/v3/me
     * @Param should the returned object contain a list of children one level down
     */
    public Optional<User> getMe(boolean includeChildren) {
        try {
            retrofit2.Response<User> response = api.getMe(includeChildren).execute();
            if (response.code() == 404) {
                return  Optional.absent();
            }
            else if (response.isSuccessful()) {
                return Optional.of(response.body());
            }
            else {
                throw new NimbitsClientException(new Exception(response.errorBody().string()));
            }
        } catch (Exception e) {
            throw new NimbitsClientException(e);
        }
    }

    /**
     * @return the authentication user from /service/v3/me
     * @Param should the returned object contain a list of children one level down
     */
    public Optional<User> getMe() {

        return getMe(false);

    }


    /**
     * if you are using an admin id, you can add users here.
     *
     * @param newUser a complete user object without an id or uuid
     * @return
     */
    public User addUser(User newUser) {

        try {
            retrofit2.Response<User> response = api.addUser(newUser).execute();

            return response.body();


        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }



    //READ Data

    /**
     * @param entity
     * @param start
     * @param end
     * @param mask   nullable - if present, will be used to filter values based on the mask string - can be null, string or regex
     * @return
     */
    public List<Value> getValues(Entity entity, Date start, Date end, String mask) {
        return api.getData(entity.getId(), start.getTime(), end.getTime(), mask);
    }

    public List<Value> getValues(Entity entity, Date start, Date end, Integer count, String mask) {
        return api.getData(entity.getId(), start.getTime(), end.getTime(), mask, count);
    }

    public List<Value> getValues(Entity entity, Integer count) {
        return api.getData(entity.getId(), count);
    }

    /**
     * @param entity
     * @param start
     * @param end
     * @return
     */
    public List<Value> getValues(Entity entity, Date start, Date end) {
        return api.getData(entity.getId(), start.getTime(), end.getTime());
    }

    public Value getSnapshot(Point point) {
        ValueContainer valueContainer = api.getSnapshot(point.getId());
        return valueContainer.getSnapshot();


    }

    public Value getSnapshot(String pointName) {
        Optional<Point> pointOptional = findPointByName(pointName);
        if (pointOptional.isPresent()) {
            ValueContainer valueContainer = api.getSnapshot(pointOptional.get().getId());
            return valueContainer.getSnapshot();
        } else {
            throw new RuntimeException("Point Not Found");
        }


    }

    public Value getSnapshot(Entity entity) {
        ValueContainer valueContainer = api.getSnapshot(entity.getId());
        return valueContainer.getSnapshot();


    }


    //Write Data

    /**
     * Record a series of values to a data point
     *
     * @param entity
     * @param values
     */
    public void recordValues(Entity entity, List<Value> values) {
        api.recordData(entity.getId(), values, new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {

            }
        });
    }

    private Value recordValuesSync(Entity entity, Value value) {
        return api.recordSnapshotSync(entity.getId(), value);
    }

    public void recordValue(Point point, Value newValue) {
        recordValues(point, Collections.singletonList(newValue));
    }

    public void recordValue(String pointName, Value newValue) {
        Optional<Point> point = findPointByName(pointName);
        if (point.isPresent()) {
            recordValues(point.get(), Collections.singletonList(newValue));
        } else {
            throw new RuntimeException("Point Not Found");
        }
    }

    //DELETE Entities

    public void deleteEntity(Entity entity) {
        api.deleteEntity(entity.getId(), new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {

            }
        });
    }

    //Create Entity methods


    /**
     * Add an entity as a child of a parent
     *
     * @param parent
     * @param point
     * @return
     */
    @Deprecated //we'll be creating individual methods for creating different types of entities
    public Entity addEntity(Entity parent, Point point) {

        Entity e = api.addEntity(parent.getId(), point);
        return e;
    }

    public Category addCategory(Entity parent, Category category) {
        return api.addCategory(parent.getId(), category);
    }

    public WebHook addWebHook(Entity parent, WebHook webHook) {
        return api.addWebhook(parent.getId(), webHook);
    }

    public Subscription addSubscription(Entity parent, Subscription subscription) {
        return api.addSubscription(parent.getId(), subscription);

    }


    public Sync addSync(Entity parent, Sync e) {
        return api.addSync(parent.getId(), e);

    }

    public Calculation addCalc(Entity parent, Calculation e) {
        return api.addCalc(parent.getId(), e);

    }


    public Summary addSummary(Entity parent, Summary e) {
        return api.addSummary(parent.getId(), e);
    }


    /**
     * Add an point as a child of a parent
     *
     * @param parent
     * @param point
     * @return
     */
    public Point addPoint(Entity parent, Point point) {

        return api.addPoint(parent.getId(), point);

    }


    public List<Point> getNearbyPoints(Point localPoint, double meters) {
        return api.getNearbyPoints(localPoint.getId(), meters);
    }


    public Instance addInstance(Entity parent, Instance instance) {
        return api.addInstance(parent.getId(), instance);
    }

    public Schedule addSchedule(Entity parent, Schedule s) {
        return api.addSchedule(parent.getId(), s);
    }


    //find entity methods

    /**
     * get all children under an entity
     *
     * @param parent
     * @return
     */

    public List<Entity> getChildren(Entity parent) {

        if (parent != null) {
            return api.getChildren(parent.getId());
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<Point> findPointByName(String pointName) {


        Point p = api.findPoint(pointName);

        return Optional.of(p);

    }

    public Optional<Category> findCategory(String name) {


        return Optional.of(api.findCategory(name, EntityType.category.getCode()));


    }

    public Optional<WebHook> findWebHook(String name) {


        return Optional.of(api.findWebHook(name, EntityType.webhook.getCode()));


    }

    public Optional<Subscription> findSubscription(String name) {

        return Optional.of(api.findSubscription(name, EntityType.subscription.getCode()));


    }

    public Optional<Sync> findSync(String name) {


        return Optional.of(api.findSync(name, EntityType.sync.getCode()));


    }

    public Optional<Calculation> findCalculation(String name) {


        return Optional.of(api.findCalculation(name, EntityType.calculation.getCode()));

    }

    public Optional<Instance> findInstance(String name) {


        return Optional.of(api.findInstance(name, EntityType.instance.getCode()));

    }


    public Optional<Schedule> findSummary(String name) {


        return Optional.of(api.findSchedule(name, EntityType.schedule.getCode()));


    }


    public Point getPoint(String uuid) {
        return api.getPoint(uuid);
    }

    public boolean entityExists(String uuid) {
        Entity e;

        try {
            e = api.getEntity(uuid);
            return (e != null);
        } catch (Exception ex) {
            return false;
        }

    }

    public Optional<User> findUser(String email) {

        return Optional.of(api.findUser(email, EntityType.user.getCode()));


    }


    public void updateEntity(Entity entity) {
        api.updateEntity(entity.getId(), entity, new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {

            }
        });
    }


    /**
     * @param entity   a valid entity with a key and updated values
     * @param callback returnes success or error
     */
    public void updateEntity(Entity entity, Callback<Void> callback) {
        api.updateEntity(entity.getId(), entity, callback);
    }

    public void setSnapshot(String pointName, Value value, Callback<Void> callback) {
        Point p = api.findPoint(pointName);
        api.setSnapshot(p.getId(), value, callback);
    }


    public Value recordValueSync(String pointName, Value value) {
        Optional<Point> point = findPointByName(pointName);
        if (point.isPresent()) {
            return recordValuesSync(point.get(), value);
        } else {
            throw new RuntimeException("Point Not Found");
        }
    }

    public Optional<User> getUser(String email) {

        try {
            retrofit2.Response<User> response = api.getUser(email).execute();
            if (response.code() == 404) {
                return Optional.absent();
            }
            else if (response.isSuccessful()) {
                return Optional.of(response.body());
            }
            else {
                throw new NimbitsClientException(new Exception(response.errorBody().string()));
            }
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }

    public void deleteUser(String email) {
        api.deleteUser(email, new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {

            }
        });
    }

    public static class Builder {

        private String email;
        private String token;
        private String instance;


        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public Nimbits create() {
            try {
                return new Nimbits(email, token, instance);
            } catch (Exception e) {
                throw new NimbitsClientException(e);
            }

        }

        @Deprecated //user Builder()
        private static class NimbitsBuilder {

            private String email;
            private String token;
            private String instance;


            public NimbitsBuilder email(String email) {
                this.email = email;
                return this;
            }

            public NimbitsBuilder token(String token) {
                this.token = token;
                return this;
            }

            public NimbitsBuilder instance(String instance) {
                this.instance = instance;
                return this;
            }

            public Nimbits create() {
                try {
                    return new Nimbits(email, token, instance);
                } catch (Exception e) {
                    throw new NimbitsClientException(e);
                }
            }
        }
    }
}



