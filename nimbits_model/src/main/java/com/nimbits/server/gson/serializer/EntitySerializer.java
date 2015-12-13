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

package com.nimbits.server.gson.serializer;

import com.google.gson.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.gson.GsonFactory;

import java.lang.reflect.Type;


public class EntitySerializer implements JsonSerializer<Entity> {

    @Override
    public JsonElement serialize(final Entity src, final Type type, final JsonSerializationContext jsonSerializationContext) {
       return GsonFactory.getInstance(true).toJsonTree(src);
        //return new JsonPrimitive(j);
    }
}
