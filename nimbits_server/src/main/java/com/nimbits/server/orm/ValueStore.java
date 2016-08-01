package com.nimbits.server.orm;

import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.Date;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ValueStore  {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    protected String id;

    @Persistent
    private long timestamp;

    @Persistent
    private String entityId;

    @Persistent
    private Double lat;

    @Persistent
    private Double lng;

    @Persistent
    private BigDecimal d;

    @Persistent
    private String data;

    @Persistent
    private String meta;


    public ValueStore(String entityId, Value value) {
        this.timestamp = value.getTimestamp() == null ? System.currentTimeMillis() : value.getTimestamp().getTime();
        this.entityId = entityId;
        this.lat = value.getLatitude();
        this.lng = value.getLongitude();
        this.d = BigDecimal.valueOf(value.getDoubleValue());
        this.meta = value.getMetaData();
        this.data = value.getData();


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
        return new Value.Builder().doubleValue(d.doubleValue()).lat(lat).lng(lng).timestamp(new Date(timestamp)).data(data).meta(meta).create();
    }
}
