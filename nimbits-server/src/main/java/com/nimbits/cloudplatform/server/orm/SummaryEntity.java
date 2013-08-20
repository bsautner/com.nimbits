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

package com.nimbits.cloudplatform.server.orm;

import com.nimbits.cloudplatform.client.enums.SummaryType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.summary.Summary;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:49 AM
 */

@SuppressWarnings("unused")
@PersistenceCapable
public class SummaryEntity extends TriggerEntity implements Summary {

    @Persistent
    private Integer summaryType;

    @Persistent
    private Long summaryIntervalMs;

    @Persistent
    private Date lastProcessed;


    protected SummaryEntity() {
    }

    public SummaryEntity(final Summary summary)  {
        super(summary);
        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();

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
    public int getSummaryIntervalSeconds() {
        return (int) (summaryIntervalMs / 1000);
    }

    @Override
    public Date getLastProcessed() {
        return lastProcessed;
    }

    public void setSummaryType(final SummaryType summaryType) {
        this.summaryType = summaryType.getCode();
    }

    public void setSummaryIntervalMs(final Long summaryIntervalMs) {
        this.summaryIntervalMs = summaryIntervalMs;
    }

    @Override
    public void setLastProcessed(final Date lastProcessed) {
        this.lastProcessed = new Date(lastProcessed.getTime());
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void update(final Entity update)  {
        super.update(update);
        final Summary summary = (Summary)update;
        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();
    }
}
