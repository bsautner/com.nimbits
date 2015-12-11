/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.hal.Embedded;
import com.nimbits.client.model.hal.Links;
import com.nimbits.client.model.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public abstract class EntityModel implements Serializable, Comparable<Entity>, Entity {

    @Expose
    private String name;

    @Expose
    private String key;

    @Expose
    private String description;

    @Expose
    protected int entityType;

    @Expose
    private int protectionLevel;

    private int alertType;

    @Expose
    private String parent;

    @Expose
    private String owner;

    private boolean readOnly = false;

    @Expose
    private String uuid;

    private Date dateCreated;

    @Expose
    private ArrayList<Entity> children;

    private String instanceUrl;
    private boolean isCached = false;

    @Expose
    private String id;
    private String action;



    //HAL
    @SerializedName("_links")
    @Expose
    private Links links;

    @SerializedName("_embedded")
    @Expose
    private Embedded embedded;



    public EntityModel(final String key,
                       final CommonIdentifier name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String parent,
                       final String owner,
                       final String uuid) {
        if (protectionLevel == null) {
            this.protectionLevel = ProtectionLevel.everyone.getCode();
        }
        else {
            this.protectionLevel = protectionLevel.getCode();
        }
        this.key = key;
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;

        this.alertType = AlertType.OK.getCode();
        this.uuid = uuid;
        this.dateCreated = new Date();

    }

    public EntityModel() {
        if (id != null && key == null) {
            key = id;
        }
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
    public void setKey(final String key) {
        this.key = key;
        this.id = key;
    }

    @Override
    public String getInstanceUrl() {
        return instanceUrl;
    }

    @Override
    public boolean isCached() {
        return this.isCached;

    }

    @Override
    public void setIsCached(boolean isCached) {
        this.isCached = isCached;
    }

    @Override
    public Action getAction() {
        Action a = Action.get(this.action);
        return a == null ? Action.none : a;
    }

    @Override
    public void setAction(Action action) {
        this.action = action.getCode();
    }

    @Override
    public List<Entity> getChildren() {
        return children;
    }

    @Override
    public void setChildren(final List<Entity> someChildren) {
        this.children = (ArrayList<Entity>) someChildren;
    }

    @Override
    public void update(Entity update) {
        this.description = update.getDescription();
        this.name = update.getName().getValue();
        this.protectionLevel = update.getProtectionLevel().getCode();
        this.parent = update.getParent();
        this.uuid = update.getUUID();
    }


    @Override
    public EntityName getName() {

        return CommonFactory.createName(name, EntityType.get(entityType));

    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(final String uuid) {
        this.uuid = uuid;
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
    public String getKey() {
        if (id != null && this.key == null) {
            key = id;
        }
        //sometimes incoming json omits the full key so we add it if missing
        if (getEntityType() != null && getEntityType().equals(EntityType.point) && this.key != null && ! key.startsWith(owner)) {
            this.key = owner + "/" + this.key;
        }
        return this.key;
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
    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.get(protectionLevel);
    }

    @Override
    public void setProtectionLevel(final ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel.getCode();
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
        if (user != null && this.owner.equals(user.getKey())) {
            return true;
        }
        if (user != null && user.getAccessKeys() != null) {
            for (AccessKey key : user.getAccessKeys()) {
                if (key.getAuthLevel().equals(AuthLevel.admin)) {
                    return true;
                }
            }

        }
        return false;

    }

    @Override
    public Date getDateCreated() {

        return dateCreated != null ? new Date(dateCreated.getTime()) : new Date();
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
        if (!this.owner.equals(user.getKey()) && this.entityType != EntityType.user.getCode()) {
            throw new IllegalArgumentException("You can't create an entity with an owner other than yourself!");
        }
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        if (dateCreated != null) {
            this.dateCreated = new Date(dateCreated.getTime());
        } else {


            this.dateCreated = null;
        }
    }

    @Override
    public boolean entityIsReadable(final User user) {


        boolean retVal = this.getEntityType().equals(EntityType.user) ||
                isOwner(user) ||
                this.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                this.getProtectionLevel().equals(ProtectionLevel.onlyConnection);


        if (this.getEntityType().equals(EntityType.summary) && user == null) {
            retVal = true; //this is a system request from the summary cron job.
        }
        if (this.getEntityType().equals(EntityType.accessKey)) {

        }
        return retVal;


    }


    @SuppressWarnings({"NonFinalFieldReferenceInEquals", "CastToConcreteClass"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityModel)) return false;

        EntityModel that = (EntityModel) o;

        if (alertType != that.alertType) return false;
        if (entityType != that.entityType) return false;
        if (protectionLevel != that.protectionLevel) return false;
        if (readOnly != that.readOnly) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + entityType;
        result = 31 * result + protectionLevel;
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
                ", protectionLevel=" + protectionLevel +
                ", entityType=" + entityType +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    public static abstract class EntityBuilder<T>  {

        protected EntityName name;

        protected String key;

        protected String description;

        protected EntityType entityType;

        protected ProtectionLevel protectionLevel;

        protected int alertType;

        protected String parent;

        protected String owner;

        protected boolean readOnly = false;

        protected String uuid;

        protected String id;

        protected String action;



        public EntityBuilder() {
        }

        public abstract T parent(String parent);

        public abstract T  name(EntityName name);

        public abstract T key(String key);

        public abstract T description(String description);

        public abstract T protectionLevel(ProtectionLevel protectionLevel);

        public abstract T alertType(int alertType);

        public abstract T owner(String owner);

        public abstract T readOnly(boolean readOnly);

        public abstract T id(String id);

        public abstract T uuid(String uuid);

        public abstract T action(String action);

    }


}
