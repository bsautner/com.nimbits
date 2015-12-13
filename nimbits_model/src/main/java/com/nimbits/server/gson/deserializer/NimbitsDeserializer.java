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

package com.nimbits.server.gson.deserializer;

import com.google.gson.*;
import com.nimbits.client.enums.EntityType;
import com.nimbits.server.gson.GsonFactory;

import java.lang.reflect.Type;


public class NimbitsDeserializer<T>  implements JsonDeserializer<T>  {



    private final Class modelClass;

    public NimbitsDeserializer(EntityType entityType) {


        this.modelClass = entityType.getModel();



    }

    @Override
    public T deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//        JsonObject object = (JsonObject) jsonElement;
//        String s = jsonElement.getAsString();
//        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
//        final String json = jsonPrimitive.getAsString();
        return (T) GsonFactory.getInstance(true).fromJson(jsonElement, modelClass);


    }
}
