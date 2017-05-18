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

package com.nimbits.client.model.sync;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.trigger.TriggerModel;


public class SyncModel extends TriggerModel implements Sync {

    @Expose
    private String targetInstance;
    @Expose
    private String accessKey;

    public SyncModel(String id, CommonIdentifier name, String description, EntityType entityType, String parent, String owner, String target, String trigger, boolean enabled, String targetInstance, String accessKey) {
        super(id, name, description, entityType, parent, owner, target, trigger, enabled);
        this.targetInstance = targetInstance;
        this.accessKey = accessKey;
    }

    @Override
    public String getTargetInstance() {
        return targetInstance;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }


    @SuppressWarnings("unused")
    public SyncModel() {
    }


    public static class Builder extends TriggerBuilder {

        private final EntityType type = EntityType.sync;


        private String targetInstance;
        private String accessKey;


        public Builder targetInstance(String targetInstance) {
            this.targetInstance = targetInstance;
            return this;
        }


        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder password(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }


        @Override
        public Builder target(String v) {
            this.target = v;
            return this;
        }

        @Override
        public Builder target(Entity v) {
            this.target = v.getId();
            return this;
        }

        public Builder trigger(Entity v) {
            this.trigger = v.getId();
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
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public Sync create() {

            this.enabled = true;


            return new SyncModel(id, name, description, type, parent, owner, target,
                    trigger, enabled, targetInstance, accessKey);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Sync c) {
            super.init(c);
            targetInstance = c.getTargetInstance();
            accessKey = c.getAccessKey();
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
    }


}
