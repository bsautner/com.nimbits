package com.nimbits.server.orm;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Key;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.summary.*;

import javax.jdo.annotations.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:49 AM
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class SummaryEntity implements Summary {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String uuid;

    @Persistent
    private String entity;

    @Persistent
    private String targetPointUUID;

    @Persistent
    private Integer summaryType;

    @Persistent
    private Long summaryIntervalMs;

    @Persistent
    private Date lastProcessed;


    public SummaryEntity() {
    }

    public SummaryEntity(Summary summary) {
        this.uuid = summary.getUuid();
        this.entity = summary.getEntity();
        this.targetPointUUID = summary.getTargetPointUUID();
        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();

    }

    public SummaryEntity(final String uuid,
                         final String entity,
                         final String targetPointUUID,
                         final SummaryType summaryType,
                         final Long summaryIntervalMs,
                         final Date lastProcessed,
                         final ProtectionLevel protectionLevel) {

        this.uuid = uuid;
        this.entity = entity;
        this.targetPointUUID = targetPointUUID;
        this.summaryType = summaryType.getCode();
        this.summaryIntervalMs = summaryIntervalMs;
        this.lastProcessed = lastProcessed;

    }


    public Key getId() {
        return id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getEntity() {
        return entity;
    }

    @Override
    public String getTargetPointUUID() {
        return targetPointUUID;
    }

    @Override
    public SummaryType getSummaryType() {
        return SummaryType.get(summaryType);
    }

    @Override
    public long getSummaryIntervalMs() {
        return  summaryIntervalMs;
    }

    @Override
    public int getSummaryIntervalHours() {
        return (int) (summaryIntervalMs / (1000 * 60 * 60));
    }

    @Override
    public Date getLastProcessed() {
        return lastProcessed;
    }

    public void setSummaryType(SummaryType summaryType) {
        this.summaryType = summaryType.getCode();
    }

    public void setSummaryIntervalMs(Long summaryIntervalMs) {
        this.summaryIntervalMs = summaryIntervalMs;
    }

    public void setLastProcessed(Date lastProcessed) {
        this.lastProcessed = lastProcessed;
    }
}
