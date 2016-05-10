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

package com.nimbits.client.model.schedule;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;

public class ScheduleModel extends EntityModel implements Serializable, Schedule {
    @Expose
    private Boolean enabled;
    @Expose
    private Long interval;
    @Expose
    private String source;
    @Expose
    private String target;
    @Expose
    private Long lastProcessed;


    private ScheduleModel() {

    }

    protected ScheduleModel(String id, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, Boolean enabled, Long interval, String source, String target, Long lastProcessed) {
        super(id, name, description, entityType, protectionLevel, parent, owner);
        this.enabled = enabled;
        this.interval = interval;
        this.source = source;
        this.target = target;
        this.lastProcessed = lastProcessed;
    }



    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public Long getInterval() {
        return interval;
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
    public void setLastProcessed(Long lastProcessed) {
        this.lastProcessed = lastProcessed;
    }

    @Override
    public Long getLastProcessed() {

        return lastProcessed == null ? 0 : lastProcessed;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }


    public static class Builder extends EntityBuilder {
        private final EntityType type = EntityType.schedule;

        private Boolean enabled;

        private Long interval;

        private String source;

        private String target;

        private Long lastProcessed;



        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public Schedule create() {
            if (protectionLevel == null) {
                protectionLevel = ProtectionLevel.everyone;
            }

            if (enabled == null) {
                enabled = true;
            }

            if (lastProcessed == null) {
                lastProcessed = 0L;
            }


            return new ScheduleModel(id, name, description,type, protectionLevel, parent, owner,
                    enabled, interval, source, target, lastProcessed);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        private void initEntity(Entity anEntity) {


            this.id = anEntity.getId();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel();
            this.alertType = anEntity.getAlertType().getCode();


        }

        public Builder init(Schedule e) {
            initEntity(e);

            enabled = e.isEnabled();

            interval = e.getInterval();

            source = e.getSource();

            target = e.getTarget();

            lastProcessed = e.getLastProcessed();

            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
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
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder interval(Long interval) {
            this.interval = interval;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }


        public Builder source(Entity source) {
            this.source = source.getId();
            return this;
        }

        public Builder target(String target) {
            this.target = target;
            return this;
        }

        public Builder target(Entity target) {
            this.target = target.getId();
            return this;
        }

        public Builder lastProcessed(Long lastProcessed) {
            this.lastProcessed = lastProcessed;
            return this;
        }


    }
}
