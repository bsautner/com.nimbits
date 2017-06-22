package com.nimbits.client.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.value.Value;

import java.io.Serializable;


public class MessagePayload implements Serializable {

    private String id;
    private EntityType entityType;
    private EventType eventType;
    private Value value;

    public MessagePayload(String id, EntityType entityType, EventType eventType) {
        this.id = id;
        this.entityType = entityType;
        this.eventType = eventType;
    }

    public MessagePayload(String id, EntityType entityType, EventType eventType, Value value) {
        this.id = id;
        this.entityType = entityType;
        this.eventType = eventType;
        this.value = value;
    }

    public MessagePayload() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("entity_type")
    public String getEntityType() {
        return entityType.name();
    }

    @JsonProperty("entity_type")
    public void setEntityType(String entityType) {
        this.entityType = EntityType.getName(entityType);
    }

    @JsonProperty("event_type")
    public String getEventType() {
        return eventType.name();
    }

    @JsonProperty("event_type")
    public void setEventType(String eventType) {
        this.eventType = EventType.getName(eventType);
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
