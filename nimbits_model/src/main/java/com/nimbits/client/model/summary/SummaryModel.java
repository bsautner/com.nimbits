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

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.trigger.TargetEntity;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.trigger.TriggerEntity;
import com.nimbits.client.model.trigger.TriggerModel;

import java.util.Date;


public class SummaryModel extends TriggerModel implements Summary {

    @Expose
    private Integer summaryType;
    @Expose
    private Long summaryIntervalMs;

    private Date lastProcessed;


    public SummaryModel(String key, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String uuid, String target, String trigger, boolean enabled, Integer summaryType, Long summaryIntervalMs, Date lastProcessed) {
        super(key, name, description, entityType, protectionLevel, parent, owner, uuid, target, trigger, enabled);
        this.summaryType = summaryType;
        this.summaryIntervalMs = summaryIntervalMs;
        this.lastProcessed = lastProcessed;
    }

    public SummaryModel(Summary summary) {
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


    public static class Builder extends TriggerBuilder {


        private SummaryType summaryType;

        private Long summaryIntervalMs;

        private Date lastProcessed;


        public Builder setSummaryType(SummaryType summaryType) {
            this.summaryType = summaryType;
            return this;
        }

        public Builder setSummaryIntervalMs(Long summaryIntervalMs) {
            this.summaryIntervalMs = summaryIntervalMs;
            return this;
        }

        public Builder setLastProcessed(Date lastProcessed) {
            this.lastProcessed = lastProcessed;
            return this;
        }

        @Override
        public Builder target(String v) {
            this.target = v;
            return this;
        }

        @Override
        public Builder trigger(String v) {
            this.trigger = v;
            return this;
        }

        @Override
        public Builder enabled(boolean v) {
            this.enabled = v;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, EntityType.summary);
            return this;
        }

        public Summary create() {
            if (protectionLevel == null) {
                protectionLevel = ProtectionLevel.everyone;
            }


            return new SummaryModel(key, name, description, EntityType.summary, protectionLevel, parent, owner, uuid,target,
                    trigger, enabled,  summaryType.getCode(), summaryIntervalMs, lastProcessed);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        @Override
        public Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        private void initEntity(Trigger anEntity) {
            this.trigger = anEntity.getTrigger();
            this.target = anEntity.getTarget();
            this.enabled = anEntity.isEnabled();


            this.key = anEntity.getKey();
            this.id = anEntity.getKey();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel();
            this.alertType = anEntity.getAlertType().getCode();
            this.uuid = anEntity.getUUID();

        }

        public Builder init(Summary c) {
            initEntity(c);
            this.summaryType = c.getSummaryType();
            this.summaryIntervalMs = c.getSummaryIntervalMs();
            this.lastProcessed = c.getLastProcessed();
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }
        @Override
        public Builder key(String key) {
            this.key = key;
            return this;
        }
        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        @Override
        public Builder protectionLevel(ProtectionLevel protectionLevel) {
            this.protectionLevel = protectionLevel;
            return this;
        }
        @Override
        public Builder alertType(int alertType) {
            this.alertType = alertType;
            return this;
        }
        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }
        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }
        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        @Override
        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }
    }
}
