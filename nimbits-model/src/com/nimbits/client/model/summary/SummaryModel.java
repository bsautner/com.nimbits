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

    private String source;
    private String target;
    private Integer summaryType;
    private Long summaryIntervalMs;
    private Date lastProcessed;


    public SummaryModel(
            final Entity entity,
            final String targetEntity,
            final String target,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed ) throws NimbitsException {
        super(entity);
        this.source = targetEntity;
        this.target = target;
        this.summaryType = summaryType.getCode();
        this.summaryIntervalMs = summaryIntervalMs;
        this.lastProcessed = new Date(lastProcessed.getTime());

    }

    public SummaryModel(Summary summary) throws NimbitsException {
       super(summary);
        this.source = summary.getSource();
        this.target = summary.getTarget();
        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();

    }

    protected SummaryModel() {
    }



    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getTarget() {
        return target;
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
    public int getSummaryIntervalSeconds() {
        return (int) (summaryIntervalMs / 1000);
    }

    @Override
    public Date getLastProcessed() {
        return lastProcessed;
    }

    @Override
    public void setLastProcessed(Date date) {
        this.lastProcessed = new Date(date.getTime());
    }


}
