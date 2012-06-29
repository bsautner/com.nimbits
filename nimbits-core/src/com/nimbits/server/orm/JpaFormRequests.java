package com.nimbits.server.orm;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 12:55 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@javax.persistence.Table(name = "FORM_REQUESTS", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaFormRequests {
    private long formRequestsId;

    @javax.persistence.Column(name = "FORM_REQUESTS_ID")
    @Id
    public long getFormRequestsId() {
        return formRequestsId;
    }

    public void setFormRequestsId(long formRequestsId) {
        this.formRequestsId = formRequestsId;
    }

    private String fromEmail;

    @javax.persistence.Column(name = "FROM_EMAIL")
    @Basic
    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    private String contact;

    @javax.persistence.Column(name = "CONTACT")
    @Basic
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String description;

    @javax.persistence.Column(name = "DESCRIPTION")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaFormRequests that = (JpaFormRequests) o;

        if (formRequestsId != that.formRequestsId) return false;
        if (contact != null ? !contact.equals(that.contact) : that.contact != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (fromEmail != null ? !fromEmail.equals(that.fromEmail) : that.fromEmail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (formRequestsId ^ (formRequestsId >>> 32));
        result = 31 * result + (fromEmail != null ? fromEmail.hashCode() : 0);
        result = 31 * result + (contact != null ? contact.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
