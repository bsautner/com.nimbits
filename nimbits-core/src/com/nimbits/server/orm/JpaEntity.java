package com.nimbits.server.orm;



import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 2:50 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@javax.persistence.IdClass(com.nimbits.server.orm.JpaEntityPK.class)
@javax.persistence.Table(name = "ENTITY", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaEntity {
    private int fkInstance;

    public JpaEntity() {
    }

    public JpaEntity(com.nimbits.client.model.entity.Entity p) {
        //To change body of created methods use File | Settings | File Templates.
    }

    @javax.persistence.Column(name = "FK_INSTANCE")
    @Id
    public int getFkInstance() {
        return fkInstance;
    }

    public void setFkInstance(int fkInstance) {
        this.fkInstance = fkInstance;
    }

    private int idEntity;

    @javax.persistence.Column(name = "ID_ENTITY")
    @Id
    public int getIdEntity() {
        return idEntity;
    }

    public void setIdEntity(int idEntity) {
        this.idEntity = idEntity;
    }

    private String uuid;

    @javax.persistence.Column(name = "UUID")
    @Basic
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private String entityName;

    @javax.persistence.Column(name = "ENTITY_NAME")
    @Basic
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    private String entityDesc;

    @javax.persistence.Column(name = "ENTITY_DESC")
    @Basic
    public String getEntityDesc() {
        return entityDesc;
    }

    public void setEntityDesc(String entityDesc) {
        this.entityDesc = entityDesc;
    }

    private int entityType;

    @javax.persistence.Column(name = "ENTITY_TYPE")
    @Basic
    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    private Timestamp ts;

    @javax.persistence.Column(name = "TS")
    @Basic
    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    private boolean active;

    @javax.persistence.Column(name = "ACTIVE")
    @Basic
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaEntity jpaEntity = (JpaEntity) o;

        if (active != jpaEntity.active) return false;
        if (entityType != jpaEntity.entityType) return false;
        if (fkInstance != jpaEntity.fkInstance) return false;
        if (idEntity != jpaEntity.idEntity) return false;
        if (entityDesc != null ? !entityDesc.equals(jpaEntity.entityDesc) : jpaEntity.entityDesc != null) return false;
        if (entityName != null ? !entityName.equals(jpaEntity.entityName) : jpaEntity.entityName != null) return false;
        if (ts != null ? !ts.equals(jpaEntity.ts) : jpaEntity.ts != null) return false;
        if (uuid != null ? !uuid.equals(jpaEntity.uuid) : jpaEntity.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fkInstance;
        result = 31 * result + idEntity;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + (entityDesc != null ? entityDesc.hashCode() : 0);
        result = 31 * result + entityType;
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
