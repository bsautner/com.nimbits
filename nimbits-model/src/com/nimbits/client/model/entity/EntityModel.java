package com.nimbits.client.model.entity;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 11:06 AM
 */
public class EntityModel  implements Serializable, Comparable<Entity>, Entity {


    private String name;
    private String key;
    private String description;
    private int entityType;
    private int protectionLevel;
    private int alertType;
    private String entity;
    private String parent;
    private String owner;
    private boolean readOnly = false;
    private String blobKey;
    private String uuid;
    private static final long serialVersionUID =3455345353L;
    private List<Point> children;

    public EntityModel(final CommonIdentifier name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String parent,
                       final String owner,
                       final String blobKey,
                       final String uuid) {
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;
        this.protectionLevel = protectionLevel.getCode();
        this.alertType = AlertType.OK.getCode();
        this.blobKey = blobKey;
        this.uuid = uuid;

    }
    public EntityModel() {
    }
    public EntityModel(final Entity anEntity) throws NimbitsException {
     if (anEntity != null) {
        this.key = anEntity.getKey();
        this.name = anEntity.getName().getValue();
        this.description = anEntity.getDescription();
        this.entityType = anEntity.getEntityType().getCode();
        this.entity =anEntity.getKey();
        this.parent = anEntity.getParent();
        this.owner = anEntity.getOwner();
        this.protectionLevel = anEntity.getProtectionLevel().getCode();
        this.alertType = anEntity.getAlertType().getCode();
        this.blobKey = anEntity.getBlobKey();
        this.uuid = anEntity.getUUID();
    }
    }

    @Override
    public List<Point> getChildren() {
        return children;
    }

    @Override
    public void setChildren(final List<Point> someChildren) {
        this.children = someChildren;
    }

    @Override
    public void update(Entity update) throws NimbitsException {
        this.description = update.getDescription();
        this.name = update.getName().getValue();
        this.protectionLevel = update.getProtectionLevel().getCode();
        this.parent = update.getParent();
        this.blobKey = update.getBlobKey();
        this.uuid = update.getUUID();
    }


    @Override
    public EntityName getName() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createName(name, EntityType.get(entityType));
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
        return (this.entity);
    }

    @Override
    public String getParent() {
        return (parent);
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
        return (owner);
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


    @Override
    public String getBlobKey() {
        return blobKey;
    }
    @Override
    public void setBlobKey(final String blobKey) {
        this.blobKey = blobKey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(Entity that) {
        int type = EntityType.get(this.entityType).getOrder().compareTo(that.getEntityType().getOrder());
        try {
            return type == 0 ? this.name.compareTo(that.getName().getValue()) : type;
        } catch (NimbitsException e) {
            return -1;
        }
    }
}
