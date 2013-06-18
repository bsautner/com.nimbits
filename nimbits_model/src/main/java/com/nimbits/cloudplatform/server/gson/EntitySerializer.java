package com.nimbits.cloudplatform.server.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.lang.reflect.Type;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 1:56 PM
 */
public class EntitySerializer implements JsonSerializer<Entity> {

    @Override
    public JsonElement serialize(final Entity src, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final String j = GsonFactory.getSimpleInstance().toJson(src);
        return new JsonPrimitive(j);
    }
}
