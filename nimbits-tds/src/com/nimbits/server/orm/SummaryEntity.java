/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;

import com.google.appengine.api.datastore.Key;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.summary.Summary;

import javax.jdo.annotations.*;
import java.util.Date;

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
        this.uuid = summary.getKey();
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
    public String getKey() {
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
