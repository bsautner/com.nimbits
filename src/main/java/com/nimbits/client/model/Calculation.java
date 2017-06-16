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

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Calculation extends Listener implements Serializable {
    private final static EntityType entityType = EntityType.calculation;


    @Persistent
    private String formula;


    @Persistent
    @Join(column = "CALC_ID_OID")
    @Element(column = "TOPIC_ID_EID")
    private List<Topic> topics;

    public Calculation() {
        super(entityType);
    }

    protected Calculation(String name, String description, String parent, String owner, List<Topic> targets, List<Topic> triggers, boolean enabled, String formula, boolean execute) {
        super(name, description, entityType, parent, owner, targets, triggers, enabled, execute);
        this.formula = formula;
        this.topics = topics;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Calculation update = (Calculation) entity;
        this.formula = update.getFormula();
        this.topics = update.getTopics();
    }


    @JsonProperty(value = "topics")
    public List<Topic> getTopics() {
        return topics;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @JsonProperty(value = "topics")
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getFormula() {
        return formula;
    }


    public static class Builder extends TriggerBuilder {


        private String formula;


        public Builder formula(String formula) {
            this.formula = formula;
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

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }

        public Calculation create() {

            return new Calculation(name, description, parent, owner, targets,
                    triggers, enabled, formula,  execute);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Calculation c) {
            super.init(c);
            this.formula = c.getFormula();
            this.execute = c.isExecute();
            this.enabled = c.isEnabled();

            return this;
        }


        public Builder execute(boolean execute) {
            this.execute = execute;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Calculation that = (Calculation) o;

        if (!formula.equals(that.formula)) return false;
        return topics.equals(that.topics);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + formula.hashCode();
        result = 31 * result + topics.hashCode();
        return result;
    }
}
