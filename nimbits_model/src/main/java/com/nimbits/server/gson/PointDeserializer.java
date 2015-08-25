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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;

import java.lang.reflect.Type;


public class PointDeserializer implements JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        //  final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        //  final String json = jsonElement.getAsString();
        Point retObj = GsonFactory.getInstance().fromJson(jsonElement, PointModel.class);
        return retObj;
    }
}
