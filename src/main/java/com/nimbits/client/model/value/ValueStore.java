package com.nimbits.client.model.value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.jdo.annotations.*;
import java.math.BigDecimal;

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ValueStore {

    @PrimaryKey
    @Unique
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
    protected String id;

    @Persistent
    @Index
    private long timestamp;

    @Persistent
    @Index
    private String entityId;

    @Persistent
    @Index
    private String meta;

    @Persistent
    private Double lat;

    @Persistent
    private Double lng;

    @Persistent
    @Column(length = 9, scale = 5)
    private BigDecimal d;

    @Persistent
    @Column(jdbcType = "CLOB")
    private String data;


    public ValueStore(String entityId, Value value) {
        this.timestamp = value.getTimestamp() == null ? System.currentTimeMillis() : value.getTimestamp();
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
