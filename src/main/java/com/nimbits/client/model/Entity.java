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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.hal.Embedded;
import com.nimbits.client.model.hal.Links;
import com.nimbits.client.model.hal.Self;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.webhook.WebHook;
import org.apache.commons.lang3.StringUtils;

import javax.jdo.annotations.*;
import java.util.List;

//TODO regen equals hashcode


@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({

        @JsonSubTypes.Type(value=User.class, name="user"),
        @JsonSubTypes.Type(value=Topic.class, name="topic"),
        @JsonSubTypes.Type(value=Group.class, name="group"),
        @JsonSubTypes.Type(value=Subscription.class, name="subscription"),
        @JsonSubTypes.Type(value=Sync.class, name="sync"),
        @JsonSubTypes.Type(value=Calculation.class, name="calculation"),
        @JsonSubTypes.Type(value=Summary.class, name="summary"),
        @JsonSubTypes.Type(value=Instance.class, name="instance"),
        @JsonSubTypes.Type(value=Schedule.class, name="schedule"),
        @JsonSubTypes.Type(value=WebHook.class, name="webhook"),
        @JsonSubTypes.Type(value=Links.class, name="links"),
        @JsonSubTypes.Type(value=Self.class, name="self"),
        @JsonSubTypes.Type(value=Filter.class, name="filter"),
        @JsonSubTypes.Type(value=Event.class, name="event")



        })
public abstract class Entity  {

    @PrimaryKey
    @Unique
    @Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
    private String id;

    @Persistent
    @Index
    private String name;

    @Persistent
    private String description;

    @Persistent
    @Index @JsonIgnore
    protected int entityType;

    @Persistent
    @Index
    private String parent;

    @Persistent
    @Index
    private String owner;

    @NotPersistent
    private List<Entity> children;


    //HAL
    @JsonProperty("_links")
    @NotPersistent
    private Links links;

    @JsonProperty("_embedded")
    @NotPersistent
    private Embedded embedded;


    public Entity(
                  final String name,
                  final String description,
                  final EntityType entityType,
                  final String parent,
                  final String owner) {

        if (name != null) {
            this.name = name;
        } else {
            this.name = (entityType.name() + "_" + System.currentTimeMillis());
        }
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;



    }

    public Entity() {

    }

    public Entity(EntityType entityType) {
        this.entityType = entityType.getCode();
    }

    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }

    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }


    public void setLinks(Links links) {
        this.links = links;
    }


    public List<Entity> getChildren() {
        return children;
    }


    public void setChildren(final List<Entity> someChildren) {
        this.children = someChildren;
    }


    public void update(Entity update) {
        this.description = update.getDescription();
        this.name = update.getName();
        this.parent = update.getParent();
    }


    public String getName() {

        return CommonFactory.createName(name, EntityType.get(entityType));

    }


    public String getId() {
        return id;
    }


    public void setId(final String id) {
        this.id = id;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(final String description) {
        this.description = description;
    }

    public String getParent() {
        return parent;
    }



    public void setParent(final String parent) {
        this.parent = parent;
    }

    public String getOwner() {
        return owner;
    }


    public void setOwner(final String owner) {
        this.owner = owner;
    }


    public boolean isOwner(final User user) {
        return user != null && (this.owner.equals(user.getId()));


    }

    public Links getLinks() {
        return links;
    }

    public void validate(User user) {
        if (StringUtils.isEmpty(this.owner) || StringUtils.isEmpty(this.name) || StringUtils.isEmpty(this.parent)) {
            throw new IllegalArgumentException("Entity was missing required data, validation failed"
                    + "owner=" + owner + "name=" + this.name + "parent:" + this.parent);
        }

        if (StringUtils.isEmpty(this.owner)) {
            throw new IllegalArgumentException("Owner must not be null");
        }
        if (!this.owner.equals(user.getId()) && this.entityType != EntityType.user.getCode()) {
            throw new IllegalArgumentException("You can't create an entity with an owner other than yourself!");
        }
    }


    @SuppressWarnings({"NonFinalFieldReferenceInEquals", "CastToConcreteClass"})
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity that = (Entity) o;


        if (entityType != that.entityType) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")

    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + entityType;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "EntityModel{" +
                ", owner='" + owner + '\'' +
                ", parent='" + parent + '\'' +
                ", entityType=" + entityType +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    public static abstract class EntityBuilder<T> {

        protected String name;

        protected String description;

        protected String parent;

        protected String owner;



        public EntityBuilder() {
        }

        public abstract T parent(String parent);

        public abstract T name(String name);

        public abstract T description(String description);

        public abstract T owner(String owner);

        public void init(Entity anEntity) {

            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();


        }

    }


}
