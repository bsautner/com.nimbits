/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.trigger.Trigger;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.Date;


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

    public SummaryEntity(final Summary summary) {
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
        return summaryIntervalMs;
    }

    @Override
    public int getSummaryIntervalSeconds() {
        return (int) (summaryIntervalMs / 1000);
    }

    @Override
    public Date getLastProcessed() {
        if (lastProcessed != null) {
            return new Date(lastProcessed.getTime());
        } else {
            return new Date(0);
        }
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
    public void update(final Entity update) {
        super.update(update);
        final Summary summary = (Summary) update;
        this.summaryType = summary.getSummaryType().getCode();
        this.summaryIntervalMs = summary.getSummaryIntervalMs();
        this.lastProcessed = summary.getLastProcessed();
    }

    @Override
    public void init(Entity anEntity) {

    }

    @Override
    public void init(Trigger entity) {

    }
}
