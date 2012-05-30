package com.nimbits.server.orm;



import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 2:50 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
 @javax.persistence.Table(name = "ENTITY", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaEntity implements com.nimbits.client.model.entity.Entity {

    @javax.persistence.Column(name = "ENTITY_NAME")
    @Basic
    private String entityName;

    @javax.persistence.Column(name = "INSTANCE_URL")
    private String instanceUrl;

    @javax.persistence.Column(name = "ID_ENTITY")
    @Id
    private int idEntity;

    @javax.persistence.Column(name = "UUID")
    @Basic
    private String uuid;

    @javax.persistence.Column(name = "TS")
    @Basic
    private Timestamp ts;

    @javax.persistence.Column(name = "ENTITY_DESC")
    @Basic
    private String entityDesc;

    @javax.persistence.Column(name = "ENTITY_TYPE")
    @Basic
    private int entityType;

    @javax.persistence.Column(name = "ACTIVE")
    @Basic
    private boolean active;



    public JpaEntity() {
    }

    public JpaEntity(com.nimbits.client.model.entity.Entity p, String instanceUrl) throws NimbitsException {
        this.entityName = p.getName().getValue();
        this.entityDesc = p.getDescription();
        this.active = true;
        this.entityType = p.getEntityType().getCode();
        this.ts = new Timestamp(p.getDateCreated().getTime());
        this.uuid = p.getUUID();
        this.instanceUrl = instanceUrl;
    }


    @Override
    public EntityName getName() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createName(this.entityName, EntityType.get(this.entityType));
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setName(EntityName name) throws NimbitsException {
       this.entityName = name.getValue();
    }

    @Override
    public String getDescription() {
        return this.entityDesc;
    }

    @Override
    public void setDescription(String description) {
       this.entityDesc = description;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }

    @Override
    public void setEntityType(EntityType entityType) {
      this.entityType = entityType.getCode();
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getParent() {
        return null;
    }

    @Override
    public void setParent(String parent) {

    }

    @Override
    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.everyone;
    }

    @Override
    public void setProtectionLevel(ProtectionLevel protectionLevel) {

    }

    @Override
    public String getOwner() {
        return null;
    }

    @Override
    public void setOwner(String owner) {

    }

    @Override
    public AlertType getAlertType() {
        return AlertType.OK;
    }

    @Override
    public void setAlertType(AlertType alertType) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public void setChildren(List<Point> children) {

    }

    @Override
    public void update(com.nimbits.client.model.entity.Entity update) throws NimbitsException {

    }

    @Override
    public List<Point> getChildren() {
        return null;
    }

    @Override
    public boolean isOwner(User user) {
        return false;
    }

    @Override
    public boolean entityIsReadable(User user) throws NimbitsException {
        return false;
    }

    @Override
    public Date getDateCreated() {
        return new Date(this.ts.getTime());
    }

    @Override
    public void validate() throws NimbitsException {

    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.ts = new Timestamp(dateCreated.getTime());
    }

    @Override
    public void setKey(String key) throws NimbitsException {

    }

    @Override
    public String getInstanceUrl() {
        return instanceUrl;
    }

    @Override
    public int compareTo(com.nimbits.client.model.entity.Entity entity) {
        return 0;
    }
}
