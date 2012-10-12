/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/6/12
 * Time: 6:24 PM
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class EntityStore implements Entity {

    private static final long serialVersionUID = 5539781607214211456L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;

    @Persistent
    private String name;

    @Persistent
    private String uuid;

    @Persistent
    private String description;

    @Persistent
    private Integer entityType;

    @Persistent
    private Integer protectionLevel;

    @Persistent
    private String parent;

    @Persistent
    String owner;

    @NotPersistent
    private int alertType;

    @Persistent
    Date dateCreated;

    @NotPersistent
    private boolean readOnly;

    @SuppressWarnings("unused")
    protected EntityStore() {

    }


    public EntityStore(final Entity entity) throws NimbitsException {

        final EntityName saferName = CommonFactory.createName(entity.getName().getValue(), entity.getEntityType());
        try {
            final Class cls = Class.forName(entity.getEntityType().getClassName());


            setKey(cls, entity, saferName);
            this.uuid = entity.getUUID();
            this.name = saferName.getValue();
            this.description = entity.getDescription();
            this.entityType = entity.getEntityType().getCode();
            this.parent = entity.getParent();
            this.owner = entity.getOwner();
            this.protectionLevel = entity.getProtectionLevel().getCode();

        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    public EntityStore(final Class<?> cls, final Entity entity) throws NimbitsException {

        final EntityName saferName = CommonFactory.createName(entity.getName().getValue(), entity.getEntityType());
        setKey(cls, entity, saferName);
        this.uuid = entity.getUUID();
        this.name = saferName.getValue();
        this.description = entity.getDescription();
        this.entityType = entity.getEntityType().getCode();
        this.parent = entity.getParent();
        this.owner = entity.getOwner();
        this.protectionLevel = entity.getProtectionLevel().getCode();


    }



    private void setKey(final Class<?> cls, final Entity entity, final CommonIdentifier saferName) {
        this.key = Utils.isEmptyString(entity.getKey())
                ? entity.getEntityType().equals(EntityType.user)
                ? KeyFactory.createKey(cls.getSimpleName(), saferName.getValue())
                : entity.getEntityType().equals(EntityType.point)
                ? KeyFactory.createKey(cls.getSimpleName(), entity.getOwner() + '/' + saferName.getValue())
                : KeyFactory.createKey(cls.getSimpleName(), UUID.randomUUID().toString()) : KeyFactory.createKey(cls.getSimpleName(), entity.getKey());
    }

    @Override
    public EntityName getName() {
        try {
            return name != null ? CommonFactory.createName(name, EntityType.get(this.entityType)) : null;
        } catch (NimbitsException e) {
            return null;
        }
        catch (NullPointerException e) {
            return null;
        }
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
    public void setName(final EntityName name) throws NimbitsException {
        final EntityName saferName = CommonFactory.createName(name.getValue(), EntityType.get(this.entityType));
        this.name = saferName.getValue();
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

        return  this.key  == null ? null : this.key.getName();
    }

//    @Override
//    public void setEntity(String entity) {
//        this.entity = entity;
//    }

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
        this.alertType= alertType.getCode();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }


    @Override
    public void setChildren(final List<Point> children) {

    }


    @Override
    public void update(final Entity update) throws NimbitsException {
        this.description = update.getDescription();
        this.name = update.getName().getValue();
        this.protectionLevel = update.getProtectionLevel().getCode();
        this.parent = update.getParent();

        if (this.dateCreated==null) {
            this.dateCreated = new Date();
        }
        this.uuid = update.getUUID();

    }

    @Override
    public List<Point> getChildren() {
        return null;
    }

    @Override
    public boolean isOwner(final User user) {
        return false;
    }

    @Override
    public boolean entityIsReadable(final User user) throws NimbitsException {
       throw new NimbitsException("Not Implemented");
    }


    @Override
    public void validate(User user) throws NimbitsException {

       if (Utils.isEmptyString(this.owner) || Utils.isEmptyString(this.name) || Utils.isEmptyString(this.parent)) {
          throw new NimbitsException("Entity was missing required data, validation failed"
          + "owner=" + owner + "name=" + this.name + "parent:" + this.parent);
       }
    }
    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = new Date(dateCreated.getTime());
    }

    @Override
    public void setKey(String key) throws NimbitsException {
       throw new NimbitsException("Not Implemented");
    }

    @Override
    public String getInstanceUrl() {
        return null;
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public void setIsCached(boolean isCached) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public int compareTo(final Entity entity) {
        return 0;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityStore)) return false;

        EntityStore that = (EntityStore) o;

        if (alertType != that.alertType) return false;
        if (readOnly != that.readOnly) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (protectionLevel != null ? !protectionLevel.equals(that.protectionLevel) : that.protectionLevel != null)
            return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (protectionLevel != null ? protectionLevel.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + alertType;
        result = 31 * result + (readOnly ? 1 : 0);
        return result;
    }


}
