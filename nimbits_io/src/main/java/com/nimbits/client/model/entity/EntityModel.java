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

package com.nimbits.client.model.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.hal.Embedded;
import com.nimbits.client.model.hal.Links;
import com.nimbits.client.model.user.User;

import java.io.Serializable;
import java.util.List;


public abstract class EntityModel implements Serializable, Comparable<Entity>, Entity {

    @Expose
    private String name;

    @Expose
    private String id;

    @Expose
    private String description;

    @Expose
    protected int entityType;

    private int alertType;

    @Expose
    private String parent;

    @Expose
    private String owner;

    private boolean readOnly = false;


    @Expose
    private List<Entity> children;


    private String action;


    //HAL
    @SerializedName("_links")
    @Expose
    private Links links;

    @SerializedName("_embedded")
    @Expose
    private Embedded embedded;


    public EntityModel(final String id,
                       final CommonIdentifier name,
                       final String description,
                       final EntityType entityType,
                       final String parent,
                       final String owner) {


        this.id = id;
        if (name != null) {
            this.name = name.getValue();
        } else {
            this.name = (entityType.name() + "_" + System.currentTimeMillis());
        }
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;

        this.alertType = AlertType.OK.getCode();


    }

    public EntityModel() {

    }


    @Override
    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }

    @Override
    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public List<Entity> getChildren() {
        return children;
    }

    @Override
    public void setChildren(final List<Entity> someChildren) {
        this.children = someChildren;
    }

    @Override
    public void update(Entity update) {
        this.description = update.getDescription();
        this.name = update.getName().getValue();
        this.parent = update.getParent();
        this.id = update.getId();
    }

    @Override
    public void init(Entity anEntity) {


        this.id = anEntity.getId();
        this.name = anEntity.getName().getValue();
        this.description = anEntity.getDescription();
        this.entityType = anEntity.getEntityType().getCode();
        this.parent = anEntity.getParent();
        this.owner = anEntity.getOwner();

        this.alertType = anEntity.getAlertType().getCode();


    }


    @Override
    public EntityName getName() {

        return CommonFactory.createName(name, EntityType.get(entityType));

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public void setName(final EntityName name) {
        this.name = name.getValue();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }

    @Override
    public void setEntityType(final EntityType entityType) {
        this.entityType = entityType.getCode();
    }


    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void setParent(final String parent) {
        this.parent = parent;
    }


    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    @Override
    public AlertType getAlertType() {
        return AlertType.get(this.alertType);
    }

    @Override
    public void setAlertType(final AlertType alertType) {
        this.alertType = alertType.getCode();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }


    @SuppressWarnings("MethodWithMultipleReturnPoints")
    @Override
    public int compareTo(final Entity that) {
        final int type = EntityType.get(this.entityType).getOrder().compareTo(that.getEntityType().getOrder());

        return type == 0 ? this.name.compareTo(that.getName().getValue()) : type;

    }

    @Override
    public boolean isOwner(final User user) {
        return user != null && (this.owner.equals(user.getId()));


    }


    @Override
    public void validate(User user) {
        if (Utils.isEmptyString(this.owner) || Utils.isEmptyString(this.name) || Utils.isEmptyString(this.parent)) {
            throw new IllegalArgumentException("Entity was missing required data, validation failed"
                    + "owner=" + owner + "name=" + this.name + "parent:" + this.parent);
        }

        if (Utils.isEmptyString(this.owner)) {
            throw new IllegalArgumentException("Owner must not be null");
        }
        if (!this.owner.equals(user.getId()) && this.entityType != EntityType.user.getCode()) {
            throw new IllegalArgumentException("You can't create an entity with an owner other than yourself!");
        }
    }


    @SuppressWarnings({"NonFinalFieldReferenceInEquals", "CastToConcreteClass"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityModel)) return false;

        EntityModel that = (EntityModel) o;

        if (alertType != that.alertType) return false;
        if (entityType != that.entityType) return false;
        if (readOnly != that.readOnly) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + entityType;
        result = 31 * result + alertType;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        return result;
    }

    @Override()
    public String toString() {
        return "EntityModel{" +
                ", readOnly=" + readOnly +
                ", owner='" + owner + '\'' +
                ", parent='" + parent + '\'' +
                ", alertType=" + alertType +
                ", entityType=" + entityType +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    public static abstract class EntityBuilder<T> {

        protected EntityName name;

        protected String description;

        protected EntityType entityType;

        protected int alertType;

        protected String parent;

        protected String owner;

        protected boolean readOnly = false;

        protected String id;

        protected String action;


        public EntityBuilder() {
        }

        public abstract T parent(String parent);

        public abstract T name(EntityName name);

        public abstract T description(String description);

        public abstract T alertType(int alertType);

        public abstract T owner(String owner);

        public abstract T readOnly(boolean readOnly);

        public abstract T id(String id);

        public abstract T action(String action);


        public void init(Entity anEntity) {


            this.id = anEntity.getId();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();

            this.alertType = anEntity.getAlertType().getCode();


        }

    }


}
