package com.nimbits.cloudplatform.server.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.lang.reflect.Type;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/17/11
 * Time: 2:55 PM
 */
public class ValueSerializer implements JsonSerializer<Value> {

    @Override
    public JsonElement serialize(Value src, Type typeOfSrc, JsonSerializationContext context) {
        final String j = GsonFactory.getSimpleInstance().toJson(src);
        return new JsonPrimitive(j);
    }
}
