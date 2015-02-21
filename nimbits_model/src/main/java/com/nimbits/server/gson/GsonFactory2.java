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

import com.google.gson.*;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;

import java.lang.reflect.Type;


public class GsonFactory2 {


    public static Gson getInstance() {
        return new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializer()).create();
    }


    private static class ValueDeserializer implements JsonDeserializer<Value> {

        @Override
        public Value deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String s = json.toString();
            return new GsonBuilder().create().fromJson(s, ValueModel.class);
        }
    }
}
