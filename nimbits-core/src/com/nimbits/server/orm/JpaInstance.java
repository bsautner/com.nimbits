package com.nimbits.server.orm;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.instance.Instance;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 2:50 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@javax.persistence.Table(name = "INSTANCES", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaInstance implements Instance {
    public JpaInstance() {
    }

    public JpaInstance(String baseUrl, String ownerEmail, String serverVersion, boolean active, Timestamp ts) {
        this.baseUrl = baseUrl;
        this.ownerEmail = ownerEmail;
        this.serverVersion = serverVersion;
        this.active = active;
        this.ts = ts;
    }

    public JpaInstance(Instance i) throws NimbitsException {
        this.baseUrl = i.getBaseUrl();
        this.ownerEmail = i.getOwnerEmail().getValue();
        this.serverVersion = i.getVersion();

    }


    @Column(name = "ID_INSTANCE", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @javax.persistence.Column(name = "BASE_URL")
    @Basic
    private String baseUrl;

    @javax.persistence.Column(name = "OWNER_EMAIL")
    @Basic
    private String ownerEmail;



    public void setIdServer(int id) {
        this.id = id;
    }


    @Override
    public int getId() {
        return id;
          }


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }



    public EmailAddress getOwnerEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(ownerEmail);
    }

    @Override
    public String getVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setOwnerEmail(EmailAddress ownerEmail) {
        this.ownerEmail = ownerEmail.getValue();
    }
    @javax.persistence.Column(name = "CODE_VERSION")
    @Basic
    private String serverVersion;


    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
    @javax.persistence.Column(name = "ACTIVE")
    @Basic
    private boolean active;


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


}
