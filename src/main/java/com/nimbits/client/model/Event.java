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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.topic.Topic;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Event extends Listener implements Serializable {

    private final static EntityType entityType = EntityType.event;

    @Persistent
    private int eventType;

    @Persistent
    private double value;


    @SuppressWarnings("unused")
    public Event() {

        super(entityType);
    }


    public Event(String name, String description, EntityType entityType, String parent, String owner, List<Topic> targets,
                 List<Topic> triggers, boolean enabled, EventType eventType, double value, boolean execute) {
        super(name, description, entityType, parent, owner, targets, triggers, enabled, execute);
        this.eventType = eventType.getCode();
        this.value = value;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Event update = (Event) entity;
        this.eventType = update.getEventType().getCode();
        this.value = update.getValue();
    }

    @JsonIgnore
    public EventType getEventType() {
        return EventType.get(eventType);
    }

    public double getValue() {
        return value;
    }

    @JsonProperty(value = "event_type")
    public void setEventType(String eventType) {
        this.eventType = EventType.valueOf(eventType).getCode();
    }

    @JsonProperty(value = "event_type")
    public String eventType() {
       EventType eventType = EventType.get(this.eventType);
       if (eventType != null) {
           return eventType.name();
       }
       else {
           return EventType.none.name();
       }
    }

    @JsonIgnore
    public void setEventType(EventType eventType) {
        this.eventType = eventType.getCode();
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static class Builder extends TriggerBuilder {


        private EventType eventType;


        private double value;


        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder eventValue(double value) {
            this.value = value;
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

        @Override
        public Builder enabled(boolean v) {
            this.enabled = v;
            return this;
        }

        @Override
        public Builder name(String name) {
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }

        @Override
        public Builder execute(boolean execute) {
            this.execute = execute;
            return this;
        }

        public Event create() {

            this.enabled = true;


            return new Event(name, description, entityType, parent, owner, targets,
                    triggers, enabled, eventType, value, execute);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Event c) {
            super.init(c);


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
