/*
 * Copyright (c) 2014 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;

import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.gson.ValueDeserializer;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SeriesApiTest {

    @Test
    public void testJson() {
        String json = "{" +
                "\"id\":\"test@example.com/foo\"," +
                "\"values\":[{\"d\":0.06267},{\"d\":0.90}]" +
                "}\n";
        System.out.println(json);
        Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializer()).create();
        Point point = gson.fromJson(json, PointModel.class);
        assertNotNull(point);
        assertFalse(point.getValues().isEmpty());
        assertNotNull(point.getKey());
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Value value = ValueFactory.createValueModel(random.nextDouble());
            point.getValues().add(value);

        }
        String r = gson.toJson(point);
        System.out.println(r);

    }

    @Test
    public void testJson2() {
        final Type listType = new TypeToken<List<PointModel>>() {
        }.getType();
        Gson gson = new GsonBuilder().registerTypeAdapter(Value.class, new ValueDeserializer()).create();
        String json = "[" +
                "{" +
                "\"id\":\"test@example.com/foo\"," +
                "\"values\":[{\"d\":0.06},{\"d\":0.90}]" +
                "}," +
                "{" +
                "\"id\":\"test@example.com/foo\"," +
                "\"values\":[{\"n\":\"notes\"},{\"d\":0.90}]" +
                "}," +
                "{" +
                "\"id\":\"test@example.com/foo\"," +
                "\"values\":[{\"lt\":-37.3333,\"lg\":23.33332,\"d\":0.06, \"dx\":\"blah blah data\"},{\"d\":0.90}]" +
                "}," +
                "{" +
                "\"id\":\"test@example.com/bar\"," +
                "\"values\":[{\"d\":4.3},{\"d\":10.0}]" +
                "}" +
                "]";
        System.out.println(json);

        List<Point> points = gson.fromJson(json, listType);

        for (Point point : points) {
            assertNotNull(point);
            assertFalse(point.getValues().isEmpty());
            assertNotNull(point.getKey());
            for (Value value : point.getValues()) {
                assertNotNull(value);
                assertNotNull(value.getDoubleValue());
            }

        }
//        Random random = new Random();
//        for (int i = 0; i < 10; i++) {
//            Value value = ValueFactory.createValueModel(random.nextDouble());
//            point.getValues().add(value);
//
//        }
//        String r = gson.toJson(point);
//        System.out.println(r);

    }



}
