package com.nimbits.io;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.ValueContainer;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.io.http.NimbitsClientException;
import com.nimbits.io.http.rest.RestClient;
import com.nimbits.server.gson.*;
import com.nimbits.server.gson.deserializer.*;
import retrofit.*;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

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

    private Nimbits(final String email, final String token, String instance) {


        final Gson gson =new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .registerTypeAdapter(AccessKey.class, new AccessKeySerializer())
                .registerTypeAdapter(AccessKey.class, new AccessKeyDeserializer())
                .registerTypeAdapter(Category.class, new CategoryDeserializer())
                .registerTypeAdapter(Point.class, new PointSerializer())
                .registerTypeAdapter(Point.class, new PointDeserializer())
                .registerTypeAdapter(Entity.class, new EntitySerializer())
                .registerTypeAdapter(Entity.class, new EntityDeserializer())
                .registerTypeAdapter(Calculation.class, new CalculationSerializer())
                .registerTypeAdapter(Calculation.class, new CalculationDeserializer())
                .registerTypeAdapter(User.class, new UserSerializer())
                .registerTypeAdapter(User.class, new SessionDeserializer())
                .registerTypeAdapter(WebHook.class, new WebHookDeserializer())
                .registerTypeAdapter(WebHook.class, new WebHookSerializer())
                .registerTypeAdapter(Subscription.class, new SubscriptionDeserializer())
                .create();


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
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        throw new NimbitsClientException("Error in Rest Adapter", retrofitError);
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
    public User getMe() {
        return api.getMe();
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

    public Category addCategory(User me, Category category) {
        return api.addCategory(me.getUUID(), category);
    }

    public WebHook addWebHook(Entity parent, WebHook webHook) {
        return api.addWebhook(parent.getUUID(), webHook);
    }

    public Subscription addSubscription(Entity parent, Subscription subscription) {
        return api.addSubscription(parent.getUUID(), subscription);

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

    public List<Point> getNearbyPoints(Point localPoint, double meters) {
        return api.getNearbyPoints(localPoint.getUUID(), meters);
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




    /**
     * Provides a way to search for an entity by the entity name. Note that some some types of entities may have the same name and
     * this method will return the first one found.
     * @param entityName
     * @param entityType
     * @See EntityType
     * @return The Entity, which may be downcasted to the type requestes (i.e Point, Calculation which extend Entity)
     */
    public Optional<Entity> findEntityByName(String entityName, EntityType entityType) {
        try {
            return Optional.of(api.findEntity(entityName, entityType.getCode()));
        } catch (Throwable e) {
            return Optional.absent();
        }
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
