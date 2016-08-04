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

package com.nimbits.server.gson.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.gson.GsonFactory;

import java.lang.reflect.Type;
import java.util.Map;


public class EntityDeserializer implements JsonDeserializer<Entity> {


    @Override
    public Entity deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final String json = jsonElement.toString();


        Map jsonMap = GsonFactory.getInstance(true).fromJson(json, Map.class);
        int t = Double.valueOf(String.valueOf(jsonMap.get("entityType"))).intValue();
        EntityType entityType = EntityType.get(t);

        return (Entity) GsonFactory.getInstance(true).fromJson(json, entityType.getClz());


    }
}
