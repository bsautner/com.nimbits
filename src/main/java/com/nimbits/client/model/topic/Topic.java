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

package com.nimbits.client.model.topic;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.CommonFactory;
import com.nimbits.client.model.Entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Topic extends Entity implements Serializable {

    private final static EntityType entityType = EntityType.topic;

    private static final int DEFAULT_EXPIRE = 90;


    @Persistent
    private int expire = DEFAULT_EXPIRE;

    @Persistent
    private String unit;

    @Persistent
    private int topicType;

    @Persistent
    private int precision;


    public Topic(String name, String description, String parent, String owner, int expire, String unit, TopicType topicType, int precision) {
        super(name, description, entityType, parent, owner);
        this.expire = expire;
        this.unit = unit;
        this.topicType = topicType.getCode();
        this.precision = precision;

    }


    @Override
    public void update(Entity entity) {
        super.update(entity);
        Topic update = (Topic) entity;
        this.unit = update.getUnit();
        this.topicType = update.getTopicType().getCode();
        this.precision = update.getPrecision();


    }

    // Constructors


    @SuppressWarnings("unused")
    public Topic() {
        super(entityType);
    }


    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    @JsonIgnore
    public TopicType getTopicType() {
        return TopicType.get(topicType);
    }

    @JsonProperty(value = "topic_type")
    public String topicType() {
        return TopicType.get(topicType).name();
    }

    @JsonProperty(value = "topic_type")
    public void topicType(String name) {
        this.topicType = TopicType.valueOf(name).getCode();
    }

    @JsonIgnore
    public void topicType(TopicType topicType) {
        this.topicType = topicType.getCode();
    }


    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public static class Builder extends EntityBuilder<Builder> {

        private int expire = DEFAULT_EXPIRE;

        private String unit;

        private TopicType topicType;

        private int precision;


        public Builder name(String v) {
            this.name = CommonFactory.createName(v, entityType);
            return this;
        }


        public Builder expire(int v) {
            this.expire = v;
            return this;
        }

        public Builder unit(String v) {
            this.unit = v;
            return this;
        }


        public Builder pointType(TopicType v) {
            this.topicType = v;
            return this;
        }

        public Builder precision(int v) {
            this.precision = v;
            return this;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Builder)) return false;

            Builder builder = (Builder) o;

            if (expire != builder.expire) return false;
            if (precision != builder.precision) return false;
            if (unit != null ? !unit.equals(builder.unit) : builder.unit != null) return false;
            return topicType == builder.topicType;
        }

        @Override
        public int hashCode() {
            int result = entityType.hashCode();
            result = 31 * result + expire;
            result = 31 * result + (unit != null ? unit.hashCode() : 0);
            result = 31 * result + topicType.hashCode();
            result = 31 * result + precision;
            return result;
        }

        public Builder init(Topic topic) {
            super.init(topic);
            this.expire = topic.getExpire();
            this.unit = topic.getUnit();
            this.topicType = topic.getTopicType();

            this.precision = topic.getPrecision();

            return this;
        }


        public Topic create() {


            if (topicType == null) {
                topicType = TopicType.basic;
            }

            return new Topic(name, description, parent, owner, expire,
                    unit, topicType, precision);
        }


        public Builder parent(String parent) {

            this.parent = parent;
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
