package com.nimbits.server.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyModel;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/17/11
 * Time: 12:13 PM
 */
public class GsonFactory {

    public final static Type pointListType = new TypeToken<List<PointModel>>() {
    }.getType();
    public final static Type accessKeyListType = new TypeToken<List<AccessKeyModel>>() {
    }.getType();
    public final static Type userListType = new TypeToken<List<UserModel>>() {
    }.getType();
    public final static Type valueListType = new TypeToken<List<ValueModel>>() {
    }.getType();
    public final static Type entityListType = new TypeToken<List<EntityModel>>() {
    }.getType();

    private GsonFactory() {
        throw new AssertionError();
    }


    private static class GsonHolder {
        static final Gson instance;

        static {


            instance = new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .addSerializationExclusionStrategy(new NimbitsExclusionStrategy(null))
                .registerTypeAdapter(AccessKey.class, new AccessKeySerializer())
                .registerTypeAdapter(AccessKey.class, new AccessKeyDeserializer())
                .registerTypeAdapter(Value.class, new ValueDeserializer())
                .registerTypeAdapter(Value.class, new ValueSerializer())
                .registerTypeAdapter(Point.class, new PointSerializer())
                .registerTypeAdapter(Point.class, new PointDeserializer())
                .registerTypeAdapter(Entity.class, new EntitySerializer())
                .registerTypeAdapter(Entity.class, new EntityDeserializer())
                .registerTypeAdapter(Calculation.class, new CalculationSerializer())
                .registerTypeAdapter(Calculation.class, new CalculationDeserializer())
                .registerTypeAdapter(User.class, new UserSerializer())
                .registerTypeAdapter(User.class, new UserDeserializer())
                        // .registerTypeAdapter(Date.class, new DateDeserializer())
                        //  .registerTypeAdapter(Date.class, new DateSerializer())
                .create();

        }
        private GsonHolder() {
        }
    }

    public static Gson getInstance() {
        return GsonHolder.instance;
    }

    public static Gson getSimpleInstance() {
        return new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .create();
    }



}
