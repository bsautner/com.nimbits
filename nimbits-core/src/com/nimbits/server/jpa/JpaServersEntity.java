package com.nimbits.server.jpa;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 1:05 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@javax.persistence.Table(name = "SERVERS", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaServersEntity {
    private int idServer;

    @javax.persistence.Column(name = "ID_SERVER")
    @Id
    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    private String baseUrl;

    @javax.persistence.Column(name = "BASE_URL")
    @Basic
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String ownerEmail;

    @javax.persistence.Column(name = "OWNER_EMAIL")
    @Basic
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    private String serverVersion;

    @javax.persistence.Column(name = "SERVER_VERSION")
    @Basic
    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
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

    private Timestamp ts;

    @javax.persistence.Column(name = "TS")
    @Basic
    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaServersEntity that = (JpaServersEntity) o;

        if (active != that.active) return false;
        if (idServer != that.idServer) return false;
        if (baseUrl != null ? !baseUrl.equals(that.baseUrl) : that.baseUrl != null) return false;
        if (ownerEmail != null ? !ownerEmail.equals(that.ownerEmail) : that.ownerEmail != null) return false;
        if (serverVersion != null ? !serverVersion.equals(that.serverVersion) : that.serverVersion != null)
            return false;
        if (ts != null ? !ts.equals(that.ts) : that.ts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idServer;
        result = 31 * result + (baseUrl != null ? baseUrl.hashCode() : 0);
        result = 31 * result + (ownerEmail != null ? ownerEmail.hashCode() : 0);
        result = 31 * result + (serverVersion != null ? serverVersion.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        return result;
    }
}
