package com.nimbits.server.gson;

import com.google.gson.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.lang.reflect.Type;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/20/11
 * Time: 1:57 PM
 */
public class EntityDeserializer implements JsonDeserializer<Entity> {
    @Override
    public Entity deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        final String json = jsonPrimitive.getAsString();
        return GsonFactory.getSimpleInstance().fromJson(json, EntityModel.class);

    }
}
