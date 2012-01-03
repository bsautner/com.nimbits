package com.nimbits.server.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nimbits.client.model.diagram.Diagram;

import java.lang.reflect.Type;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 1:56 PM
 */
public class DiagramSerializer implements JsonSerializer<Diagram> {

    @Override
    public JsonElement serialize(final Diagram src, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final String j = GsonFactory.getSimpleInstance().toJson(src);
        return new JsonPrimitive(j);
    }
}
