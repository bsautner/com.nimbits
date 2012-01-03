package com.nimbits.server.gson;

import com.google.gson.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;

import java.lang.reflect.Type;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 1:57 PM
 */
public class PointDeserializer implements JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        final String json = jsonPrimitive.getAsString();
        return GsonFactory.getPointInstance().fromJson(json, PointModel.class);
    }
}
