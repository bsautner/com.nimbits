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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.topic.Topic;

import javax.jdo.annotations.*;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Listener extends Entity {

    @Persistent
    @Join(column = "TARGET_ID")
    @Element(column = "TOPIC_ID")
    private List<Topic> targets;

    @Persistent
    @Join(column = "TRIGGER_ID")
    @Element(column = "TOPIC_ID")
    private List<Topic> triggers;

    @Persistent
    private boolean enabled;

    @Persistent
    private boolean execute;


    public Listener(EntityType entityType) {
        super(entityType);
    }


    public Listener(String name, String description, EntityType entityType,
                    String parent, String owner, List<Topic> targets,
                    List<Topic> triggers, boolean enabled, boolean execute) {
        super(name, description, entityType, parent, owner);
        this.targets = targets;
        this.triggers = triggers;
        this.enabled = enabled;
        this.execute = execute;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Listener update = (Listener) entity;
        this.targets = update.getTargets();
        this.triggers = update.getTriggers();
        this.enabled = update.isEnabled();
        this.execute = update.isExecute();

    }

    public List<Topic> getTargets() {
        return targets == null ? Collections.<Topic>emptyList() : targets;
    }


    public List<Topic> getTriggers() {
        return triggers == null ? Collections.<Topic>emptyList() : triggers;
    }


    public boolean isEnabled() {
        return enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public void setTargets(List<Topic> targets) {
        this.targets = targets;
    }


    public void setTriggers(List<Topic> triggers) {
        this.triggers = triggers;
    }


    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public static abstract class TriggerBuilder<T> extends EntityBuilder {

        protected List<Topic> targets;

        protected List<Topic> triggers;

        protected boolean enabled;

        protected boolean execute;

        public abstract T targets(List<Topic> v);

        public abstract T triggers(List<Topic> v);

        public abstract T target(Topic v);

        public abstract T trigger(Topic v);

        public abstract T enabled(boolean v);

        public abstract T execute(boolean v);

        public void init(Listener t) {
            super.init(t);
            triggers = t.getTriggers();
            targets = t.getTargets();
            enabled = t.isEnabled();
            execute = t.isExecute();
        }

    }
}
