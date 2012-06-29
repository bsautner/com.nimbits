package com.nimbits.server.orm;

import com.nimbits.client.model.email.EmailAddress;

import javax.persistence.*;
import java.util.Collection;


@javax.persistence.Table(name = "CLIENT", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaClient {
    protected JpaClient() {
    }

    public JpaClient(final EmailAddress emailAddress, final String contactPhone, final String companyName, final String clientName) {
        this.contactEmail = emailAddress.getValue();
        this.contactPhone = contactPhone;
        this.companyName = companyName;
        this.clientName = clientName;
    }

    @javax.persistence.Column(name = "CLIENT_ID", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long clientId;

    @javax.persistence.Column(name = "CONTACT_EMAIL", nullable = true, insertable = true, updatable = true, length = 25, precision = 0)
    @Basic
    private String contactEmail;

    @javax.persistence.Column(name = "CONTACT_PHONE", nullable = true, insertable = true, updatable = true, length = 25, precision = 0)
    @Basic
    private String contactPhone;

    @javax.persistence.Column(name = "COMPANY_NAME", nullable = true, insertable = true, updatable = true, length = 25, precision = 0)
    @Basic
    private String companyName;

    @javax.persistence.Column(name = "CLIENT_NAME", nullable = false, insertable = true, updatable = true, length = 25, precision = 0)
    @Basic
    private String clientName;

    @OneToMany(targetEntity = JpaClientCommunication.class, mappedBy = "client")
    private Collection clientCommunications;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaClient jpaClient = (JpaClient) o;

        if (clientId != null ? !clientId.equals(jpaClient.clientId) : jpaClient.clientId != null) return false;
        if (clientName != null ? !clientName.equals(jpaClient.clientName) : jpaClient.clientName != null) return false;
        if (companyName != null ? !companyName.equals(jpaClient.companyName) : jpaClient.companyName != null)
            return false;
        if (contactEmail != null ? !contactEmail.equals(jpaClient.contactEmail) : jpaClient.contactEmail != null)
            return false;
        if (contactPhone != null ? !contactPhone.equals(jpaClient.contactPhone) : jpaClient.contactPhone != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId != null ? clientId.hashCode() : 0;
        result = 31 * result + (contactEmail != null ? contactEmail.hashCode() : 0);
        result = 31 * result + (contactPhone != null ? contactPhone.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (clientName != null ? clientName.hashCode() : 0);
        return result;
    }
}
