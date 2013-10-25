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

package com.nimbits.server.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

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
