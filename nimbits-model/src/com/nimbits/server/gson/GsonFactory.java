package com.nimbits.server.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.model.Const;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityDescriptionModel;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.point.Calculation;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModel;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/17/11
 * Time: 12:13 PM
 */
public class GsonFactory {
    private static Gson instance;

    public final static Type pointListType = new TypeToken<List<PointModel>>() {
    }.getType();
    public final static Type entityListType = new TypeToken<List<EntityModel>>() {
    }.getType();
    public final static Type userListType = new TypeToken<List<UserModel>>() {
    }.getType();
    public final static Type valueListType = new TypeToken<List<ValueModel>>() {
    }.getType();
    public final static Type pointDescriptionListType = new TypeToken<List<EntityDescriptionModel>>() {
    }.getType();

    private GsonFactory() {
        throw new AssertionError();
    }


    public static Gson getInstance() {
        // if (instance == null) {
        instance = new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .addSerializationExclusionStrategy(new NimbitsExclusionStrategy(null))
                .registerTypeAdapter(Intelligence.class, new IntelligenceSerializer())
                .registerTypeAdapter(Intelligence.class, new IntelligenceDeserializer())
                .registerTypeAdapter(Value.class, new ValueDeserializer())
                .registerTypeAdapter(Value.class, new ValueSerializer())
                .registerTypeAdapter(Point.class, new PointSerializer())
                .registerTypeAdapter(Point.class, new PointDeserializer())
                .registerTypeAdapter(Entity.class, new EntitySerializer())
                .registerTypeAdapter(Entity.class, new EntityDeserializer())
                .registerTypeAdapter(Calculation.class, new CalculationSerializer())
                .registerTypeAdapter(Calculation.class, new CalculationDeserializer())
                        // .registerTypeAdapter(Date.class, new DateDeserializer())
                        //  .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
        //  }
        return instance;
    }

    public static Gson getSimpleInstance() {
        return new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .create();
    }

    public static Gson getPointInstance() {
        return new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .registerTypeAdapter(Intelligence.class, new IntelligenceSerializer())
                .registerTypeAdapter(Intelligence.class, new IntelligenceDeserializer())
                .registerTypeAdapter(Calculation.class, new CalculationSerializer())
                .registerTypeAdapter(Calculation.class, new CalculationDeserializer())
                .create();
    }

}
