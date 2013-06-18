package com.nimbits.cloudplatform.server.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/17/11
 * Time: 12:13 PM
 */
public enum GsonFactory {
    instance;


    private static class GsonHolder {
        static final Gson gInstance;

        static {


            gInstance = new GsonBuilder()
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
        return GsonHolder.gInstance;
    }

    public static Gson getSimpleInstance() {
        return new GsonBuilder()
                .setDateFormat(Const.GSON_DATE_FORMAT)
                .serializeNulls()
                .create();
    }


}
