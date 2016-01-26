package com.nimbits.client.io;

import com.google.common.base.Optional;
import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.file.FileModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.io.http.rest.RestClient;
import com.nimbits.server.gson.GsonFactory;
import retrofit.*;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simpler java client for interacting with the V3 REST API using hal+json
 * and basic authentication
 *
 */
@SuppressWarnings("unused")
public class Nimbits {


    private final RestClient api;

    protected Nimbits(final String email, final String token, String instance) {




        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {

                request.addHeader("Accept", "application/hal+json");
                request.addHeader("Authorization", "Basic " + email + ":" + token);  //TODO BASE64 encode this

            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(instance)
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(GsonFactory.getInstance(false)))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {


                        StringBuilder out = new StringBuilder();
                        if (retrofitError.getResponse() != null) {
                            TypedInput body = retrofitError.getResponse().getBody();
                            try {
                                if (body != null) {
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));

                                    String newLine = System.getProperty("line.separator");
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        out.append(line);
                                        out.append(newLine);
                                    }
                                }

                                // Prints the correct String representation of body.

                            } catch (IOException e) {

                            }
                        }

                        throw new NimbitsClientException(retrofitError.getMessage() + " " + out, retrofitError);

                    }
                })
                .build();

        api = restAdapter.create(RestClient.class);
    }

    public void connect() {

    }

    /**
     *
     * @return the authentication user from /service/v3/me
     */
    public User getMe(boolean includeChildren) {
        return api.getMe(includeChildren);
    }

    /**
     * if you are using an admin id, you can add users here.
     * @param newUser a complete user object without an id or uuid
     * @return
     */
    public User addUser(User newUser) {

        return api.addUser(newUser);
    }


    //READ Data

    /**
     *
     *
     * @param entity
     * @param start
     * @param end
     * @param mask nullable - if present, will be used to filter values based on the mask string - can be null, string or regex
     * @return
     */
    public List<Value> getValues(Entity entity, Date start, Date end, String mask) {
        return api.getData(entity.getUUID(), start.getTime(), end.getTime(), mask);
    }

    public List<Value> getValues(Entity entity, Integer count) {
        return api.getData(entity.getUUID(), count);
    }

    /**
     *
     *
     * @param entity
     * @param start
     * @param end

     * @return
     */
    public List<Value> getValues(Entity entity, Date start, Date end) {
        return api.getData(entity.getUUID(), start.getTime(), end.getTime());
    }

    public Value getSnapshot(Point point) {
        ValueContainer valueContainer =  api.getSnapshot(point.getUUID());
        return valueContainer.getSnapshot();


    }

    public Value getSnapshot(String pointName) {
        Optional<Point> pointOptional = findPointByName(pointName);
        if (pointOptional.isPresent()) {
            ValueContainer valueContainer = api.getSnapshot(pointOptional.get().getUUID());
            return valueContainer.getSnapshot();
        }
        else {
            throw new RuntimeException("Point Not Found");
        }


    }

    public Value getSnapshot(Entity entity) {
        ValueContainer valueContainer =  api.getSnapshot(entity.getUUID());
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
        api.recordData(entity.getUUID(), values, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                throw new RuntimeException(retrofitError);
            }
        });
    }


    public void recordValue(Point point, Value newValue) {
        recordValues(point, Collections.singletonList(newValue));
    }

    public void recordValue(String pointName, Value newValue) {
        Optional<Point> point = findPointByName(pointName);
        if (point.isPresent()) {
            recordValues(point.get(), Collections.singletonList(newValue));
        }
        else {
            throw new RuntimeException("Point Not Found");
        }
    }

    //DELETE Entities

    public void deleteEntity(Entity entity) {
        api.deleteEntity(entity.getUUID(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                throw new RuntimeException(retrofitError);
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

        Entity e  =  api.addEntity(parent.getUUID(), point);
        return  e;
    }

    public Category addCategory(Entity parent, Category category) {
        return api.addCategory(parent.getUUID(), category);
    }

    public WebHook addWebHook(Entity parent, WebHook webHook) {
        return api.addWebhook(parent.getUUID(), webHook);
    }

    public Subscription addSubscription(Entity parent, Subscription subscription) {
        return api.addSubscription(parent.getUUID(), subscription);

    }


    public Sync addSync(Entity parent, Sync e) {
        return api.addSync(parent.getUUID(), e);

    }

    public Calculation addCalc(Entity parent, Calculation e) {
        return api.addCalc(parent.getUUID(), e);

    }


    public Summary addSummary(Entity parent, Summary e) {
        return api.addSummary(parent.getUUID(), e);
    }

    public AccessKey addAccessKey(Entity parent, AccessKey e) {

        return api.addAccessKey(parent.getUUID(), e);

    }

    /**
     * Add an point as a child of a parent
     *
     * @param parent
     * @param point
     * @return
     */
    public Point addPoint(Entity parent, Point point) {

        return api.addPoint(parent.getUUID(), point);

    }

    public Connection addConnection(Entity parent, Connection c) {
        return api.addConnection(parent.getUUID(), c);
    }


    public List<Point> getNearbyPoints(Point localPoint, double meters) {
        return api.getNearbyPoints(localPoint.getUUID(), meters);
    }


    public Instance addInstance(Entity parent, Instance instance) {
        return api.addInstance(parent.getUUID(), instance);
    }

    public Schedule addSchedule(Entity parent, Schedule s) {
        return api.addSchedule(parent.getUUID(), s);
    }


    //find entity methods

    /**
     * get all children under an entity
     * @param parent
     * @return
     */

    public List<Entity> getChildren(Entity parent) {

        return api.getChildren(parent.getUUID());
    }

    public Optional<Point> findPointByName(String pointName) {
        try {
            Point p =  api.findPoint(pointName);

            if (p != null) {
                return Optional.of(p);
            }
            else {
                return Optional.absent();
            }

        } catch (Throwable throwable) {
            return Optional.absent();
        }

    }

    public Optional<Category> findCategory(String name) {

        try {
            return Optional.of(api.findCategory(name, EntityType.category.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<WebHook> findWebHook(String name) {

        try {
            return Optional.of(api.findWebHook(name, EntityType.webhook.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Subscription> findSubscription(String name) {

        try {
            return Optional.of(api.findSubscription(name, EntityType.subscription.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Sync> findSync(String name) {

        try {
            return Optional.of(api.findSync(name, EntityType.sync.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Calculation> findCalculation(String name) {

        try {
            return Optional.of(api.findCalculation(name, EntityType.calculation.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Instance> findInstance(String name) {

        try {
            return Optional.of(api.findInstance(name, EntityType.instance.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Socket> findSocket(String name) {

        try {
            return Optional.of(api.findSocket(name, EntityType.socket.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Connection> findConnection(String name) {

        try {
            return Optional.of(api.findConnection(name, EntityType.connection.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public Optional<Schedule> findSummary(String name) {

        try {
            return Optional.of(api.findSchedule(name, EntityType.schedule.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public void uploadFile(FileModel fileModel) {
       api.uploadFile(fileModel.getId(), fileModel.getEncoded(), new Callback<Void>() {
           @Override
           public void success(Void aVoid, Response response) {

           }

           @Override
           public void failure(RetrofitError retrofitError) {

           }
       });
    }

    public Optional<String> getFile(String id) {
        try {
            String s = api.getFile(id);
            return Optional.of(s);
        } catch (Exception e) {
            return Optional.absent();
        }

    }

    public void deleteFile(String id) {
        api.deleteFile(id, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public void updateFile(FileModel fileModel) {
        api.updateFile(fileModel.getId(), fileModel.getEncoded(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
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
        try {
            return Optional.of(api.findUser(email, EntityType.user.getCode()));
        } catch (Throwable e) {

            return Optional.absent();
        }

    }

    public void updateEntity(Entity entity) {
        api.updateEntity(entity.getUUID(), entity, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

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
            return new Nimbits(email, token, instance);
        }
    }

    @Deprecated //user Builder()
    public static class NimbitsBuilder {

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
