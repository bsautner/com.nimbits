package com.nimbits.io.http;

import com.google.gson.JsonDeserializer;
import com.nimbits.client.enums.EntityType;
import com.nimbits.server.gson.EntityDeserializer;
import com.nimbits.server.gson.PointDeserializer;
import com.nimbits.server.gson.deserializer.*;

public class SerializationHelper {

    public static JsonDeserializer getDeserializer(final EntityType type) {
        switch (type) {

            case user:
                return new SessionDeserializer();
            case point:
                return new PointDeserializer();
            case category:
                return new CategoryDeserializer();
            case subscription:
                return new SubscriptionDeserializer();
            case sync:
                return new SyncDeserializer();
            case calculation:
                return new CalculationDeserializer();
            case summary:
                return new SummaryDeserializer();
            case accessKey:
                return new AccessKeyDeserializer();
            case instance:
                return new InstanceDeserializer();
            case socket:
                return new SocketDeserializer();
            case connection:
                return new ConnectionDeserializer();
            case schedule:
                return new ScheduleDeserializer();
            case webhook:
                return new WebHookDeserializer();
            default:
                return new EntityDeserializer();
        }


    }
}
