package com.nimbits.server.orm;

import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ValueStore {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    protected String id;

    @Persistent
    private long timestamp;

    @Persistent
    private String entityId;

    @Persistent
    private Value value;


    public ValueStore(String entityId, Value value) {
        this.timestamp = value.getTimestamp() == null ? System.currentTimeMillis() : value.getTimestamp().getTime();
        this.entityId = entityId;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getEntityId() {
        return entityId;
    }

    public Value getValue() {
        return value;
    }
}
