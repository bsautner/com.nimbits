package com.nimbits.client.model.entity;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.value.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 11:06 AM
 */
public class EntityModel  implements Serializable, Entity {

    private static long serialVersionUID = 10L;

    private String name;
    private String description;
    private int entityType;
    private int protectionLevel;
    private int alertType;
    private String entityUUID;
    private String parentUUID;
    private String ownerUUID;
    private String[] metadata = null;
    private String[] accessKeys = null;
    private boolean readOnly = false;
    private Value value = null;

    public EntityModel(final EntityName name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String entityUUID,
                       final String parentUUID,
                       final String childUUID,
                       final String ownerUUID) {
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.entityUUID =entityUUID;
        this.parentUUID = parentUUID;
        this.ownerUUID = ownerUUID;
        this.protectionLevel = protectionLevel.getCode();
        this.alertType = AlertType.OK.getCode();

    }

    public EntityModel(final Entity entity)  {
        this.name = entity.getName().getValue();
        this.description = entity.getDescription();
        this.entityType = entity.getEntityType().getCode();
        this.entityUUID =entity.getUUID();
        this.parentUUID = entity.getParentUUID();
        this.ownerUUID = entity.getOwnerUUID();
        this.protectionLevel = entity.getProtectionLevel().getCode();
        this.alertType = entity.getAlertType().getCode();


    }

    public EntityModel() {
    }

    @Override
    public EntityName getName() {
        return CommonFactoryLocator.getInstance().createName(name);
    }

    @Override
    public void setName(EntityName name) {
        this.name = name.getValue();
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
    public String getUUID() {
        return (this.entityUUID);
    }

    @Override
    public void setUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }

    @Override
    public String getParentUUID() {
        return (parentUUID);
    }

    @Override
    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
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
    public String getOwnerUUID() {
        return (ownerUUID);
    }

    @Override
    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public AlertType getAlertType() {
        return AlertType.get(this.alertType);
    }

    @Override
    public void setAlertType(AlertType alertType) {
       this.alertType = alertType.getCode();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
