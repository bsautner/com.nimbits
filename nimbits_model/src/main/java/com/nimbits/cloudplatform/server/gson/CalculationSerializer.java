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

package com.nimbits.cloudplatform.server.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;

import java.lang.reflect.Type;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/10/11
 * Time: 7:28 PM
 */
public class CalculationSerializer implements JsonSerializer<Calculation> {

    @Override
    public JsonElement serialize(final Calculation src, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final String j = GsonFactory.getSimpleInstance().toJson(src);
        return new JsonPrimitive(j);
    }
}
