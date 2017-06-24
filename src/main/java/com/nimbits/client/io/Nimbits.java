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

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.io.http.rest.RestClient;
import com.nimbits.client.model.*;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


import java.io.IOException;
import java.util.*;

/**
 * A simpler java client for interacting with the V3 REST API using hal+json
 * and basic authentication
 */
@SuppressWarnings("unused")
public class Nimbits {


    private final RestClient api;
    private final int NOT_FOUND = 404;


    protected Nimbits(final String email, final String password, String basUrl) {


        OkHttpClient client = new OkHttpClient.Builder()

                .addNetworkInterceptor(new RequestInterceptor(email, password))
                .build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(basUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(RestClient.class);
    }

    class RequestInterceptor implements Interceptor {

        private final String email;
        private final String password;

        RequestInterceptor(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        public Response intercept(Chain chain)  {
            Request request = chain.request();


            Request newRequest;

            newRequest = request.newBuilder()

                    .addHeader("Accept", "application/json,application/hal+json")
                    .addHeader("Authorization", "Basic " + email + ":" + password)
                    .build();
            try {

                Response response = chain.proceed(newRequest);
                if (response.code() != 200 && response.code() != 404) {
                    if (response.body() != null) {
                        System.out.println(response.message());
                        throw new NimbitsClientException(response.code() + " " + response.message());

                    }

                }
                return response;
            } catch (IOException e) {
                throw  new NimbitsClientException(e.getMessage(), e);
            }
        }
    }


    /**
     * @return the authentication user from /v5/api/me
     * @Param should the returned object contain a list of children one level down
     */
    public User getMe(boolean includeChildren) {
        try {
            return api.getMe(includeChildren).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    /**
     * @return the authentication user from /v5/api/me
     * @Param should the returned object contain a list of children one level down
     */
    public User getMe() {

        try {
            return api.getMe(false).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }


    /**
     * if you are using an admin id, you can add users here.
     *
     * @param newUser a complete user object without an id or uuid
     * @return
     */
    public User addUser(User newUser) {

        try {
            return api.addUser(newUser).execute().body();

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
        try {
            return api.getData(entity.getId(), start.getTime(), end.getTime(), mask)
                    .execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public List<Value> getValues(Entity entity, Date start, Date end, Integer count, String mask) {
        try {
            return api.getData(entity.getId(), start.getTime(), end.getTime(), mask, count)
                    .execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public List<Value> getValues(Entity entity, Integer count) {
        try {
            return api.getData(entity.getId(), count) .execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }

    /**
     * @param entity
     * @param start
     * @param end
     * @return
     */
    public List<Value> getValues(Entity entity, Date start, Date end) {
        try {
            return api.getData(entity.getId(), start.getTime(), end.getTime()).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }

    public Value getSnapshot(Topic topic) {
        ValueContainer valueContainer = null;
        try {
            valueContainer = api.getSnapshot(topic.getId()).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

        return valueContainer.getSnapshot();


    }

    public Value getSnapshot(String pointName) {
        Optional<Topic> pointOptional = findTopicByName(pointName);
        if (pointOptional.isPresent()) {
            ValueContainer valueContainer = null;
            try {
                valueContainer = api.getSnapshot(pointOptional.get().getId())
                        .execute().body();
            } catch (IOException e) {
                throw new NimbitsClientException(e);
            }
            return valueContainer.getSnapshot();
        } else {
            throw new RuntimeException("Topic Not Found");
        }


    }

    public Value getSnapshot(Entity entity) {
        ValueContainer valueContainer = null;
        try {
            valueContainer = api.getSnapshot(entity.getId())
                    .execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
        return valueContainer.getSnapshot();


    }


    //Write Data

    /**
     * Record a series of values to a data topic
     *
     * @param entity
     * @param values
     */
    public void recordValues(Entity entity, List<Value> values) {
        Call<ResponseBody> call = api.recordData(entity.getId(), values);
        try {
            call.execute();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    private Value recordValuesSync(Entity entity, Value value) {
        try {
            return api.recordSnapshotSync(entity.getId(), value).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public void recordValue(Topic topic, Value newValue) {
        recordValues(topic, Collections.singletonList(newValue));
    }

    public void recordValue(String pointName, Value newValue) {
        Optional<Topic> topic = findTopicByName(pointName);
        if (topic.isPresent()) {
            recordValues(topic.get(), Collections.singletonList(newValue));
        } else {
            throw new RuntimeException("Topic Not Found");
        }
    }

    //DELETE Entities

    public void deleteEntity(Entity entity) {
        api.deleteEntity(entity.getId());
    }

    //Create Entity methods



    public Group addCategory(Entity parent, Group group) {
        try {
            return api.addCategory(parent.getId(), group).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public WebHook addWebHook(Entity parent, WebHook webHook) {
        try {
            return api.addWebhook(parent.getId(), webHook).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public Subscription addSubscription(Entity parent, Subscription subscription) {
        try {
            return api.addSubscription(parent.getId(), subscription).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }

    public Sync addSync(Entity parent, Sync e) {
        try {
            return api.addSync(parent.getId(), e).execute().body();
        } catch (IOException e1) {
            throw new NimbitsClientException(e1);
        }

    }

    public Calculation addCalc(Entity parent, Calculation e) {
        try {
            return api.addCalc(parent.getId(), e).execute().body();
        } catch (IOException e1) {
            throw new NimbitsClientException(e1);
        }

    }

    public Summary addSummary(Entity parent, Summary e) {
        try {
            return api.addSummary(parent.getId(), e).execute().body();
        } catch (IOException e1) {
            throw new NimbitsClientException(e1);
        }
    }


    /**
     * Add an topic as a child of a parent
     *
     * @param parent
     * @param topic
     * @return
     */
    public Topic addPoint(Entity parent, Topic topic) {

        try {
            retrofit2.Response<Topic> call = api.addPoint(parent.getId(), topic).execute();
            if (call.code() == 409) {
                throw new NimbitsClientException("A Topic with this name already exists");
            }
            return call.body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }


    public List<Topic> getNearbyPoints(Topic localTopic, double meters) {
        try {
            return api.getNearbyPoints(localTopic.getId(), meters).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }


    public Instance addInstance(Entity parent, Instance instance) {
        try {
            return api.addInstance(parent.getId(), instance).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public Schedule addSchedule(Entity parent, Schedule s) {
        try {
            return api.addSchedule(parent.getId(), s).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }


    //find entity methods

    /**
     * get all children under an entity
     *
     * @param parent
     * @return
     */

    public List<Entity> getChildren(Entity parent) {

        try {
            return api.getChildren(parent.getId()).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }


    public Optional<Topic> findTopicByName(String pointName) {


        Topic p;
        try {
            p = api.findTopic(pointName, EntityType.topic.getCode()).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

        return Optional.ofNullable(p);

    }


    public Optional<Group> findCategory(String name) {


        Group p;
        try {
            p = api.findCategory(name, EntityType.group.getCode())
                    .execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

        return Optional.ofNullable(p);

    }


    public Optional<WebHook> findWebHook(String name) {

        WebHook p;
        try {
            p = api.findWebHook(name, EntityType.webhook.getCode())
                    .execute().body();
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(p);

    }

    public Optional<Subscription> findSubscription(String name) {

        Subscription p;
        try {
            p = api.findSubscription(name, EntityType.subscription.getCode())
                    .execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

        return Optional.ofNullable(p);
    }

    public Optional<Sync> findSync(String name) {
        Sync p;
        try {
            p = api.findSync(name, EntityType.sync.getCode())
                    .execute().body();
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(p);
    }

    public Optional<Calculation> findCalculation(String name) {

        try {
            return Optional.ofNullable(api.findCalculation(name, EntityType.calculation.getCode()).execute().body()
            );
        } catch (IOException e) {
            return Optional.empty();
        }


    }

    public Optional<Instance> findInstance(String name) {
        try {
            return Optional.ofNullable(api.findInstance(name, EntityType.instance.getCode()).execute().body());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Summary> findSummary(String name) {
        try {
            return Optional.ofNullable(api.findSummary(name, EntityType.summary.getCode()).execute().body());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Topic getPoint(String uuid) {
        try {
            return api.getPoint(uuid).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public boolean entityExists(String uuid) {
        Entity e;

        try {
            e = api.getEntity(uuid).execute().body();
            return (e != null);
        } catch (Exception ex) {
            return false;
        }

    }

    public Optional<User> findUser(String email) {


        try {
            return Optional.ofNullable(api.findUser(email, EntityType.user.getCode()).execute().body());
        } catch (IOException e) {
           return Optional.empty();
        }


    }

    public Entity updateEntitySync(Entity entity) {
        try {
            return api.updateEntitySync(entity.getId(), entity).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public void updateEntity(Entity entity) {
        try {
            api.updateEntity(entity.getId(), entity).execute();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public void setSnapshot(String topicName, Value value) {
        Topic p;
        try {
            p = api.findTopic(topicName, EntityType.topic.getCode()).execute().body();
            if (p != null) {
                Call<ResponseBody> call = api.setSnapshot(p.getId(), value);
                call.execute();
            }
            else {
                throw new NimbitsClientException("Topic Not Found");
            }
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }

    }

    public Value recordValueSync(String pointName, Value value) {
        Optional<Topic> topic = findTopicByName(pointName);
        if (topic.isPresent()) {
            return recordValuesSync(topic.get(), value);
        } else {
            throw new RuntimeException("Topic Not Found");
        }
    }

    public User getUser(String email) {
        try {
            User user =  api.getUser(email).execute().body();
            if (user == null) {
                throw new NimbitsClientException(String.format("%s Not Found", email));
            }

            return user;
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public User deleteUser(String email) {
        try {
            return api.deleteUser(email).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public Filter addFilter(Entity parent, Filter filter) {

        addParentAsTrigger(parent, filter);
        try {
            return api.addFilter(parent.getId(), filter).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    public Event addEvent(Entity parent, Event event) {
        addParentAsTrigger(parent, event);
        try {
            return api.addEvent(parent.getId(), event).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
    }

    private void addParentAsTrigger(Entity parent, Listener filter) {
        if (filter.getTriggers().isEmpty() && parent.getEntityType().equals(EntityType.topic)) {
            List<Topic> triggers = new ArrayList<>();
            triggers.add((Topic) parent);
            filter.setTriggers(triggers);
        }
    }

    public Optional<? extends Entity> getEntity(EntityType entityType, String id) {
        Entity entity;
        try {
            entity = api.getEntity(entityType, id).execute().body();
        } catch (IOException e) {
            throw new NimbitsClientException(e);
        }
        return Optional.ofNullable(entity);
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
            return new Nimbits(email, token, instance);
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
            return new Nimbits(email, token, instance);
        }
    }


}
