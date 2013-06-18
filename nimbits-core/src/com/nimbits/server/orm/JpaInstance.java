package com.nimbits.server.orm;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.instance.Instance;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@javax.persistence.Table(name = "INSTANCE", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaInstance {

    @javax.persistence.Column(name = "ID_INSTANCE", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int idInstance;

    @javax.persistence.Column(name = "INSTANCE_URL", nullable = false, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String instanceUrl;

    @OneToMany(targetEntity = JpaEntity.class, mappedBy = "instance")
    private Collection entities;

    public int getIdInstance() {
        return idInstance;
    }

    public void setIdInstance(int idInstance) {
        this.idInstance = idInstance;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaInstance that = (JpaInstance) o;

        if (idInstance != that.idInstance) return false;
        if (instanceUrl != null ? !instanceUrl.equals(that.instanceUrl) : that.instanceUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idInstance;
        result = 31 * result + (instanceUrl != null ? instanceUrl.hashCode() : 0);
        return result;
    }
}
