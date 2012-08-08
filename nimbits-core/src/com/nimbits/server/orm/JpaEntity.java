package com.nimbits.server.orm;



import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 2:50 PM
 * Copyright 2012 Nimbits Inc - All Rights Reserved
 */
@SuppressWarnings("unused")
@Table(name = "ENTITY", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaEntity implements com.nimbits.client.model.entity.Entity {

    @Column(name = "ID_ENTITY", unique = true,nullable = false, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int idEntity;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_INSTANCE", nullable = false, insertable = true, updatable = true)
    JpaInstance instance;

    @Column(name = "ENTITY_TYPE")
    @Basic
    private int entityType;

    @Column(name = "ENTITY_NAME", nullable = false, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String entityName;




    @Column(name = "UUID", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String uuid;

    @Column(name = "TS", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    private Timestamp ts;

    @Column(name = "ENTITY_DESC", nullable = true, insertable = true, updatable = true, length = 65535, precision = 0)
    @Basic
    private String entityDesc;

    @Column(name = "ACTIVE", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    private boolean active;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public String getEntityDesc() {
        return entityDesc;
    }
    public int getIdEntity() {
        return idEntity;
    }
    public void setEntityDesc(String entityDesc) {
        this.entityDesc = entityDesc;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected JpaEntity() {
    }

    public JpaEntity(com.nimbits.client.model.entity.Entity p, JpaInstance instance) throws NimbitsException {
        this.entityName = p.getName().getValue();
        this.entityDesc = p.getDescription();
        this.active = true;
        this.entityType = p.getEntityType().getCode();
        this.ts = new Timestamp(new Date().getTime());
        this.uuid = p.getUUID();
        this.instance = instance;
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

    public void setEntityType(int entityType) {
        this.entityType = entityType;
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
        return this.instance.getInstanceUrl();
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public void setIsCached(boolean isCached) throws NimbitsException {
       throw new NimbitsException("Not Implemented");
    }


    public void setIdEntity(int idEntity) {
        this.idEntity = idEntity;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instance.setInstanceUrl(instanceUrl);
    }

    @Override
    public int compareTo(com.nimbits.client.model.entity.Entity entity) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaEntity jpaEntity = (JpaEntity) o;

        if (active != jpaEntity.active) return false;
        if (idEntity != jpaEntity.idEntity) return false;
        if (entityDesc != null ? !entityDesc.equals(jpaEntity.entityDesc) : jpaEntity.entityDesc != null) return false;
        if (entityName != null ? !entityName.equals(jpaEntity.entityName) : jpaEntity.entityName != null) return false;

        if (ts != null ? !ts.equals(jpaEntity.ts) : jpaEntity.ts != null) return false;
        if (uuid != null ? !uuid.equals(jpaEntity.uuid) : jpaEntity.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idEntity;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + (entityDesc != null ? entityDesc.hashCode() : 0);
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
