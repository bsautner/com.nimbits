package com.nimbits.server.orm;

import com.nimbits.client.model.value.Value;

import javax.jdo.annotations.*;
import java.math.BigDecimal;

@Cacheable("false")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ValueStore {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    protected String id;

    @Persistent @Index
    private long timestamp;

    @Persistent @Index
    private String entityId;

    @Persistent @Index
    private String meta;

    @Persistent
    private Double lat;

    @Persistent
    private Double lng;

    @Persistent @Column(length = 9, scale = 5)
    private BigDecimal d;

    @Persistent @Column(jdbcType = "CLOB")
    private String data;


    public ValueStore(String entityId, Value value) {
        this.timestamp = value.getLTimestamp() == null ? System.currentTimeMillis() : value.getLTimestamp();
        this.entityId = entityId;
        this.lat = value.getLatitude();
        this.lng = value.getLongitude();
        this.d = value.getDoubleValue() == null ? null : BigDecimal.valueOf(value.getDoubleValue());
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

        Value.Builder builder = new Value.Builder();

        if (d != null) {
            builder.doubleValue(d.doubleValue());
        }
        return builder.lat(lat).lng(lng).timestamp((timestamp)).data(data).meta(meta).create();
    }
}
