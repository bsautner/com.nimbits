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
import com.nimbits.client.model.topic.Topic;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@PersistenceCapable
public class Sync extends Listener implements Serializable {

    private final static EntityType entityType = EntityType.sync;


    @Persistent
    private String targetInstance;
    @Persistent
    private String accessKey;

    public Sync(String name, String description, String parent, String owner, List<Topic> targets, List<Topic> triggers, boolean enabled, String targetInstance, String accessKey, boolean execute) {
        super(name, description, entityType, parent, owner, targets, triggers, enabled, execute);
        this.targetInstance = targetInstance;
        this.accessKey = accessKey;
    }


    @Override
    public void update(Entity entity) {
        super.update(entity);
        Sync update = (Sync) entity;
        this.targetInstance = update.getTargetInstance();
        this.accessKey = update.getAccessKey();

    }

    public String getTargetInstance() {
        return targetInstance;
    }


    public String getAccessKey() {
        return accessKey;
    }


    @SuppressWarnings("unused")
    public Sync() {
        super(entityType);
    }


    public static class Builder extends TriggerBuilder {

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
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }

        public Sync create() {

            this.enabled = true;


            return new Sync(name, description, parent, owner, targets,
                    triggers, enabled, targetInstance, accessKey, execute);
        }


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
