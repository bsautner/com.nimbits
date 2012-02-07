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

package com.nimbits.server.orm.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;

import javax.jdo.annotations.*;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/6/12
 * Time: 6:24 PM
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class Entity {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String name;

    @Persistent
    private String description;

    @Persistent
    private Integer entityType;

    @Persistent
    private Integer protectionLevel;

    @Persistent
    private String entityUUID;

    @Persistent
    private String parentUUID;

    @Persistent
    private String childUUID;

    @Persistent
    private String ownerUUID;

    public Entity() {

    }

    public Entity(final EntityName name,
                  final String description,
                  final EntityType entityType,
                  final ProtectionLevel protectionLevel,
                  final UUID entityUUID,
                  final UUID parentUUID,
                  final UUID childUUID,
                  final UUID ownerUUID) {
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.entityUUID =entityUUID.toString();

        this.parentUUID = parentUUID==null ? null : parentUUID.toString();
        this.childUUID = childUUID.toString();
        this.ownerUUID = ownerUUID.toString();
        this.protectionLevel = protectionLevel.getCode();


    }



    public EntityName getName() {
        return CommonFactoryLocator.getInstance().createName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType.getCode();
    }

    public UUID getUUID() {
        return UUID.fromString(this.entityUUID);
    }

    public void setUUID(UUID entityUUID) {
        this.entityUUID = entityUUID.toString();
    }

    public UUID getParentUUID() {
        return UUID.fromString(parentUUID);
    }

    public void setParentUUID(UUID parentUUID) {
        this.parentUUID = parentUUID.toString();
    }

    public UUID getChildUUID() {
        return UUID.fromString(childUUID);
    }

    public void setChildUUID(UUID childUUID) {
        this.childUUID = childUUID.toString();
    }

    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.get(protectionLevel);
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel.getCode();
    }

    public UUID getOwnerUUID() {
        return UUID.fromString(ownerUUID);
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID.toString();
    }
}
