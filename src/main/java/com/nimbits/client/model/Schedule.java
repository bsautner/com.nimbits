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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.topic.Topic;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@PersistenceCapable
public class Schedule extends Entity implements Serializable {

    private final static EntityType entityType = EntityType.schedule;

    @Persistent
    private String cron;

    @Persistent
    private Map<String, Integer> listeners;

    @Persistent @Column(defaultValue = "")
    private String host;


    public Schedule() {
        super(entityType);
    }

    public Schedule(String name, String description, EntityType entityType, String parent, String owner, String cron,  Map<String, EntityType> listeners) {
        super(name, description, entityType, parent, owner);
        this.cron = cron;
        this.listeners = new HashMap<>();
        for (String s : listeners.keySet()) {
            this.listeners.put(s, listeners.get(s).getCode());
        }
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Schedule update = (Schedule) entity;
        this.cron = update.getCron();
        this.listeners = update.getListeners();
    }


    public String getCron() {
        return cron;
    }


    @JsonProperty(value = "listeners")
    public Map<String, Integer> getListeners() {
        return listeners == null ? Collections.emptyMap() : listeners;
    }


    @JsonProperty(value = "listeners")
    public void setListenersWithCode(Map<String, Integer> input) {
        if (this.listeners == null) {
            listeners = new HashMap<>();
        }
        for (String s : input.keySet()) {
            listeners.put(s, input.get(s));
        }

    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static class Builder extends EntityBuilder {


        private String cron;

        private  Map<String, EntityType> listeners;



        public Builder name(String name) {
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }

        public Schedule create() {



            return new Schedule(name, description, entityType, parent, owner, cron, listeners);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Schedule e) {
            super.init(e);

            cron = e.getCron();

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




        public Builder interval(String interval) {
            this.cron = interval;
            return this;
        }



        public Builder cron(String cron) {
            this.cron = cron;
            return this;
        }

        public Builder listeners(Map<String, EntityType> map) {
            listeners = map;


            return this;
        }

        public Builder listener(Listener l) {
            if (listeners == null) {
                listeners = new HashMap<>();
            }
            listeners.put(l.getId(), l.getEntityType());

            return this;
        }


    }
}
