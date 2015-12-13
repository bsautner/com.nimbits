package com.nimbits.server.gson.deserializer;

import com.google.gson.JsonDeserializer;
import com.nimbits.client.enums.EntityType;

public class SerializationHelper {

    public static JsonDeserializer getDeserializer(final EntityType type) {
        return new NimbitsDeserializer(type);

    }
}
