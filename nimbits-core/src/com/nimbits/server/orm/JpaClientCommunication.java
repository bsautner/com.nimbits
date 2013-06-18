package com.nimbits.server.orm;

import com.nimbits.client.enums.client.CommunicationType;

import javax.persistence.*;
import java.util.Date;

@javax.persistence.Table(name = "CLIENT_COMMUNICATION", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaClientCommunication {

    public JpaClientCommunication(final JpaClient client, final String message, final CommunicationType type) {
        this.clientMessage = message;
        this.client = client;
        this.communicationType = type.getCode();
        this.timestamp = new Date();
    }

    protected JpaClientCommunication() {
    }

    @javax.persistence.Column(name = "CLIENT_COMMUNICATION_ID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long clientCommunicationId;

    @javax.persistence.Column(name = "COMMUNICATION_TYPE", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private Integer communicationType;

    @javax.persistence.Column(name = "COMMUNICATION_SOURCE", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private Integer communicationSource = 0;

    @javax.persistence.Column(name = "TIMESTAMP", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Temporal( TemporalType.DATE)
    private Date timestamp;

    @javax.persistence.Column(name = "CLIENT_MESSAGE", nullable = false, insertable = true, updatable = true, length = 500, precision = 0)
    @Basic
    private String clientMessage;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_FK", nullable = false, insertable = true, updatable = true)
    JpaClient client;


    public Long getClientCommunicationId() {
        return clientCommunicationId;
    }

    public void setClientCommunicationId(Long clientCommunicationId) {
        this.clientCommunicationId = clientCommunicationId;
    }

//    public Long getClientFk() {
//        return clientFk;
//    }
//
//    public void setClientFk(Long clientFk) {
//        this.clientFk = clientFk;
//    }

    public CommunicationType getCommunicationType() {
        return CommunicationType.get(this.communicationType) ;
    }

    public void setCommunicationType(Integer communicationType) {
        this.communicationType = communicationType;
    }

    public Integer getCommunicationSource() {
        return communicationSource;
    }

    public void setCommunicationSource(Integer communicationSource) {
        this.communicationSource = communicationSource;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }



}
