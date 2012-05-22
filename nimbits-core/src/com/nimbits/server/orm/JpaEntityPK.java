package com.nimbits.server.orm;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 2:50 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
public class JpaEntityPK implements Serializable {
    private int fkInstance;

    @Id
    @Column(name = "FK_INSTANCE")
    public int getFkInstance() {
        return fkInstance;
    }

    public void setFkInstance(int fkInstance) {
        this.fkInstance = fkInstance;
    }

    private int idEntity;

    @Id
    @Column(name = "ID_ENTITY")
    public int getIdEntity() {
        return idEntity;
    }

    public void setIdEntity(int idEntity) {
        this.idEntity = idEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaEntityPK that = (JpaEntityPK) o;

        if (fkInstance != that.fkInstance) return false;
        if (idEntity != that.idEntity) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fkInstance;
        result = 31 * result + idEntity;
        return result;
    }
}
