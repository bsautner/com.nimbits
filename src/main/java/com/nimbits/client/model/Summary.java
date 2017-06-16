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

package com.nimbits.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.topic.Topic;


import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@PersistenceCapable
public class Summary extends Listener implements Serializable {

    private final static EntityType entityType = EntityType.summary;


    @Persistent
    private Integer summaryType;

    @Persistent
    private Long summaryIntervalMs;

    private long processedTimestamp;


    protected Summary(String name, String description, String parent, String owner, List<Topic> targets, List<Topic> triggers, boolean enabled, Integer summaryType, Long summaryIntervalMs, long processedTimestamp, boolean execute) {
        super(name, description, entityType, parent, owner, targets, triggers, enabled, execute);
        this.summaryType = summaryType;
        this.summaryIntervalMs = summaryIntervalMs;
        this.processedTimestamp = processedTimestamp;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Summary update = (Summary) entity;
        this.summaryType = update.getSummaryType().getCode();
        this.summaryIntervalMs = update.getSummaryIntervalMs();
        this.processedTimestamp = update.getProcessedTimestamp();
    }

    @SuppressWarnings("unused")
    public Summary() {
        super(entityType);
    }


    public SummaryType getSummaryType() {
        return SummaryType.get(summaryType);
    }


    public long getSummaryIntervalMs() {
        return summaryIntervalMs;
    }


    public int getSummaryIntervalSeconds() {
        return (int) (summaryIntervalMs / 1000);
    }


    public long getProcessedTimestamp() {
        return processedTimestamp;
    }


    public void setProcessedTimestamp(long date) {
        this.processedTimestamp = date;
    }


    public boolean isReady() {

        return this.processedTimestamp + summaryIntervalMs < new Date().getTime();

    }

    public void setSummaryType(Integer summaryType) {
        this.summaryType = summaryType;
    }

    public void setSummaryIntervalMs(Long summaryIntervalMs) {
        this.summaryIntervalMs = summaryIntervalMs;
    }


    public static class Builder extends TriggerBuilder {
        private final EntityType type = EntityType.summary;

        private SummaryType summaryType;

        private Long summaryIntervalMs;

        private long processTimestamp;


        public Builder summaryType(SummaryType summaryType) {
            this.summaryType = summaryType;
            return this;
        }

        public Builder summaryIntervalMs(Long summaryIntervalMs) {
            this.summaryIntervalMs = summaryIntervalMs;
            return this;
        }

        public Builder processedTimestamp(long processedTimestamp) {
            this.processTimestamp = processedTimestamp;
            return this;
        }


        @Override
        public Builder targets(List v) {
            if (this.targets == null) {
                this.targets = new ArrayList<Topic>(v.size());
            }
            this.targets.addAll(v);
            return this;
        }

        @Override
        public Builder triggers(List v) {
            if (this.triggers == null) {
                this.triggers = new ArrayList(v.size());
            }
            this.triggers.addAll(v);
            return this;
        }

        @Override
        public Builder target(Topic v) {
            if (this.targets == null) {
                this.targets = new ArrayList<Topic>();
            }
            this.targets.add(v);

            return this;
        }

        @Override
        public Builder trigger(Topic v) {
            if (this.triggers == null) {
                this.triggers = new ArrayList<Topic>();
            }
            this.triggers.add(v);
            return this;
        }

        public Builder enabled(boolean v) {
            this.enabled = v;
            return this;
        }


        public Builder execute(boolean execute) {
            this.execute = execute;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public Summary create() {

            this.enabled = true;

            return new Summary(name, description, parent, owner, targets,
                    triggers, enabled, summaryType.getCode(), summaryIntervalMs, processTimestamp, execute);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Summary c) {
            super.init(c);


            this.summaryType = c.getSummaryType();
            this.summaryIntervalMs = c.getSummaryIntervalMs();
            this.processTimestamp = c.getProcessedTimestamp();
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }


        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }


    }
}
