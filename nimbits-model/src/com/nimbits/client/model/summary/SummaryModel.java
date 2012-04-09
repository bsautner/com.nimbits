package com.nimbits.client.model.summary;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:59 AM
 */
public class SummaryModel extends EntityModel implements Summary {

    private String entity;
    private String targetPointUUID;
    private Integer summaryType;
    private Long summaryIntervalMs;
    private Date lastProcessed;


    public SummaryModel(

            final String entity,
            final String targetPointUUID,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed ) {

        this.entity = entity;
        this.targetPointUUID = targetPointUUID;
        this.summaryType = summaryType.getCode();
        this.summaryIntervalMs = summaryIntervalMs;
        this.lastProcessed = lastProcessed;

    }
    public SummaryModel(
            final Entity entity,
            final String targetEntity,
            final String targetPointUUID,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed ) throws NimbitsException {
        super(entity);
        this.entity = targetEntity;
        this.targetPointUUID = targetPointUUID;
        this.summaryType = summaryType.getCode();
        this.summaryIntervalMs = summaryIntervalMs;
        this.lastProcessed = lastProcessed;

    }

    public SummaryModel(Summary summary) throws NimbitsException {
       super(summary);
        this.entity = summary.getEntity();
        this.targetPointUUID = summary.getTargetPointUUID();
        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();

    }

    public SummaryModel() {
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
        return summaryIntervalMs;
    }

    @Override
    public int getSummaryIntervalHours() {
        return (int) (summaryIntervalMs / (1000 * 60 * 60));
    }

    @Override
    public Date getLastProcessed() {
        return lastProcessed;
    }


}
