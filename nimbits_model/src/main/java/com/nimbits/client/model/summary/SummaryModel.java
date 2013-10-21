/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.summary;

import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.TargetEntity;
import com.nimbits.client.model.trigger.TriggerEntity;
import com.nimbits.client.model.trigger.TriggerModel;

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
    public SummaryModel() {
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

    @Override
    public boolean isReady() {

        return this.lastProcessed.getTime() + summaryIntervalMs < new Date().getTime();

    }

    public void setSummaryType(Integer summaryType) {
        this.summaryType = summaryType;
    }

    public void setSummaryIntervalMs(Long summaryIntervalMs) {
        this.summaryIntervalMs = summaryIntervalMs;
    }
}
