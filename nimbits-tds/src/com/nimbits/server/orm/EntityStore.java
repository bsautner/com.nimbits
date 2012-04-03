/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;

import javax.jdo.annotations.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/6/12
 * Time: 6:24 PM
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class EntityStore implements Entity {

    @PrimaryKey
    @Persistent
    private Key key;

    @Persistent
    private String name;

    @Persistent
    private String description;

    @Persistent
    private Integer entityType;

    @Persistent
    private Integer protectionLevel;

    @Persistent
    private String parent;

    @Persistent
    private String owner;

    @NotPersistent
    private int alertType;

    @Persistent
    private BlobKey blobKey;

    @NotPersistent
    private boolean readOnly;

    @SuppressWarnings("unused")
    protected EntityStore() {

    }


    public EntityStore(final Entity entity) throws NimbitsException {

        EntityName saferName = CommonFactoryLocator.getInstance().createName(entity.getName().getValue(), entity.getEntityType());
        if (Utils.isEmptyString(entity.getKey())) {
            if (entity.getEntityType().equals(EntityType.user)) {
                this.key = KeyFactory.createKey(EntityStore.class.getSimpleName(), saferName.getValue());
            }
            else if (entity.getEntityType().equals(EntityType.point)) {
                this.key =  KeyFactory.createKey(EntityStore.class.getSimpleName(), entity.getOwner() + "/" + saferName.getValue());
            }
            else {
                this.key = KeyFactory.createKey(EntityStore.class.getSimpleName(), UUID.randomUUID().toString());
            }

        }
        else {
            this.key = KeyFactory.createKey(EntityStore.class.getSimpleName(), entity.getKey());
        }
        this.name = saferName.getValue();
        this.description = entity.getDescription();
        this.entityType = entity.getEntityType().getCode();
        this.parent = entity.getParent();
        this.owner = entity.getOwner();
        this.protectionLevel = entity.getProtectionLevel().getCode();
        if (! Utils.isEmptyString(entity.getBlobKey()))  {
            this.blobKey = new BlobKey(entity.getBlobKey());
        }

    }

    @Override
    public EntityName getName() {
        try {
            return CommonFactoryLocator.getInstance().createName(name, EntityType.get(this.entityType));
        } catch (NimbitsException e) {
            return null;
        }
    }

    @Override
    public void setName(EntityName name) throws NimbitsException {
        EntityName saferName = CommonFactoryLocator.getInstance().createName(name.getValue(), EntityType.get(this.entityType));
        this.name = saferName.getValue();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }

    @Override
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType.getCode();
    }

    @Override
    public String getKey() {

        return  this.key.getName();
    }

//    @Override
//    public void setEntity(String entity) {
//        this.entity = entity;
//    }

    @Override
    public String getParent() {
        return (parent);
    }

    @Override
    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.get(protectionLevel);
    }

    @Override
    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel.getCode();
    }

    @Override
    public String getOwner() {
        return (owner);
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public AlertType getAlertType() {
        return AlertType.get(this.alertType);
    }

    @Override
    public void setAlertType(AlertType alertType) {
        this.alertType=(alertType.getCode());
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

//    @Override
//    public String getUUID() {
//        return this.entity.toString();
//
//    }
//
//    @Override
//    public void setUUID(String newUUID) {
//        this.entity = newUUID;
//    }

    @Override
    public String getBlobKey() {
        return blobKey != null ? this.blobKey.getKeyString() : null;
    }

    @Override
    public void setBlobKey(String blobKey) {
        if (! Utils.isEmptyString(blobKey)) {
            this.blobKey = new BlobKey(blobKey);
        }

    }

    @Override
    public void setPoints(List<Point> points) {
        //not implemented
    }





}
