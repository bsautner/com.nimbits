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
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueData;
import com.nimbits.client.model.value.impl.ValueDataModel;
import com.nimbits.client.model.value.impl.ValueFactory;

import java.lang.reflect.Type;
import java.util.Date;


public class ValueDeserializer implements JsonDeserializer<Value> {
    @Override
    public Value deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Location location;

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement valueElement = jsonObject.get("d");
        JsonElement dataElement = jsonObject.get("dx");
        JsonElement latElement = jsonObject.get("lt");
        JsonElement lngElement = jsonObject.get("lg");
        JsonElement timestampElement = jsonObject.get("t");
        String data = dataElement == null ? null : dataElement.getAsString();
        Double lat = latElement == null || latElement.isJsonNull() ? null : latElement.getAsDouble();
        Double lng = lngElement == null || lngElement.isJsonNull() ? null : lngElement.getAsDouble();
        Double value = valueElement == null || valueElement.isJsonNull() ? null : valueElement.getAsDouble();
        Long timestamp = timestampElement == null || timestampElement.isJsonNull() ? 0 : timestampElement.getAsLong();


        if (lat != null && lng != null) {
            location = LocationFactory.createLocation(lat, lng);
        } else {
            location = LocationFactory.createEmptyLocation();
        }


        Date time = timestamp > 0 ? new Date(timestamp) : new Date();
        ValueData valueData;
        if (data != null && data.length() > 0) {
            valueData = ValueDataModel.getInstance(SimpleValue.getInstance(data));
        } else {
            valueData = ValueDataModel.getEmptyInstance();
        }

        return ValueFactory.createValueModel(location, value, time, valueData, AlertType.OK);

    }
}
