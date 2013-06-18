package com.nimbits.cloudplatform.server.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nimbits.cloudplatform.client.model.point.Point;

import java.lang.reflect.Type;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 1:56 PM
 */
public class PointSerializer implements JsonSerializer<Point> {


    @Override
    public JsonElement serialize(Point src, Type type, JsonSerializationContext jsonSerializationContext) {
        final String j = GsonFactory.getInstance().toJson(src);
        return new JsonPrimitive(j);
    }
}
