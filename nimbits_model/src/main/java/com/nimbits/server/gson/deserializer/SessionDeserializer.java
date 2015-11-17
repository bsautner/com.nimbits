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
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.gson.EntityDeserializer;

import java.lang.reflect.Type;


public class SessionDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {


        final String json = jsonElement.toString();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Entity.class, new EntityDeserializer())
                .excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(json, UserModel.class);


    }
}
