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

package com.nimbits.server.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.gson.deserializer.DateDeserializer;
import com.nimbits.server.gson.deserializer.EntityDeserializer;
import com.nimbits.server.gson.deserializer.NimbitsDeserializer;
import com.nimbits.server.gson.serializer.EntitySerializer;
import com.nimbits.server.gson.serializer.NimbitsSerializer;
import com.nimbits.server.gson.serializer.PointSerializer;

import java.util.Date;

public class GsonFactory {

    private static Gson gson;
    private static Gson excludedInstance;

    public static Gson getInstance(boolean excludeFieldsWithoutExposeAnnotation) {

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(Const.GSON_DATE_FORMAT)
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .registerTypeAdapter(Entity.class, new EntityDeserializer())
                    .registerTypeAdapter(Point.class, new PointSerializer())
                    .registerTypeAdapter(Entity.class, new EntitySerializer())
                        .excludeFieldsWithoutExposeAnnotation();


            for (EntityType t : EntityType.values()) {
                gsonBuilder
                        .registerTypeAdapter(t.getClz(), new NimbitsDeserializer<Entity>(t))
                        .registerTypeAdapter(t.getClz(), new NimbitsSerializer<Entity>());
            }
            gson = gsonBuilder.create();
        }

        if (excludedInstance == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(Const.GSON_DATE_FORMAT)
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .registerTypeAdapter(Entity.class, new EntityDeserializer())
                    .registerTypeAdapter(Point.class, new PointSerializer())
                    .registerTypeAdapter(Entity.class, new EntitySerializer());



            for (EntityType t : EntityType.values()) {
                        gsonBuilder
                        .registerTypeAdapter(t.getClz(), new NimbitsDeserializer<Entity>(t))
                        .registerTypeAdapter(t.getClz(), new NimbitsSerializer<Entity>());
            }
            excludedInstance = gsonBuilder.create();
        }


        return excludeFieldsWithoutExposeAnnotation ? gson : excludedInstance;


    }




}
