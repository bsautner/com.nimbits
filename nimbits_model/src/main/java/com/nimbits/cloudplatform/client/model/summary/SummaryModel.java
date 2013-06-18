package com.nimbits.cloudplatform.client.model.summary;

import com.nimbits.cloudplatform.client.enums.SummaryType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.trigger.TargetEntity;
import com.nimbits.cloudplatform.client.model.trigger.TriggerEntity;
import com.nimbits.cloudplatform.client.model.trigger.TriggerModel;

import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:59 AM
 */
public class SummaryModel extends TriggerModel implements Summary {

    private Integer summaryType;
    private Long summaryIntervalMs;
    private Date lastProcessed;


    public SummaryModel(
            final Entity entity,
            final TriggerEntity trigger,
            final TargetEntity target,
            final boolean enabled,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed )  {
        super(entity, trigger, target, enabled);

        this.summaryType = summaryType.getCode();
        this.summaryIntervalMs = summaryIntervalMs;
        this.lastProcessed = new Date(lastProcessed.getTime());

    }

    public SummaryModel(Summary summary)  {
       super(summary);

        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();

    }

    @SuppressWarnings("unused")
    protected SummaryModel() {
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
