package com.nimbits.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.http.NimbitsClientException;
import com.nimbits.io.http.rest.RestClient;
import com.nimbits.server.gson.*;
import com.nimbits.server.gson.deserializer.*;
import retrofit.*;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.Date;
import java.util.List;

/**
 * A simpler java client for interacting with the V3 REST API using hal+json
 * and basic authentication
 *
 */

public class Nimbits {

    private final String email;
    private final String token;
    private final String instance;
    private final RequestInterceptor requestInterceptor;
    private final RestAdapter restAdapter;
    private final RestClient api;

    private Nimbits(final String email, final String token, String instance) {
        this.email = email;
        this.token = token;
        this.instance = instance;

        final Gson gson =new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .registerTypeAdapter(AccessKey.class, new AccessKeySerializer())
                .registerTypeAdapter(AccessKey.class, new AccessKeyDeserializer())

                .registerTypeAdapter(Point.class, new PointSerializer())
                .registerTypeAdapter(Point.class, new PointDeserializer())
                .registerTypeAdapter(Entity.class, new EntitySerializer())
                .registerTypeAdapter(Entity.class, new EntityDeserializer())
                .registerTypeAdapter(Calculation.class, new CalculationSerializer())
                .registerTypeAdapter(Calculation.class, new CalculationDeserializer())
                .registerTypeAdapter(User.class, new UserSerializer())
                .registerTypeAdapter(User.class, new SessionDeserializer())
                .create();



        this.requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {

                request.addHeader("Accept", "application/hal+json");
                request.addHeader("Authorization", "Basic " + email + ":" + token);  //TODO BASE64 encode this

            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(instance)
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        throw new NimbitsClientException(retrofitError.getMessage());
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
     * @param newUser
     * @return
     */
    public User addUser(User newUser) {

        return api.addUser(newUser);
    }


    /**
     * Add an entity as a child of a parent
     *
     * @param parent
     * @param point
     * @return
     */
    public Entity addEntity(Entity parent, Point point) {

        Entity e  =  api.addEntity(parent.getUUID(), point);
        return  e;
    }

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
