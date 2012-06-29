package com.nimbits.server.transactions.dao.client;

import com.nimbits.client.enums.client.CommunicationType;
import com.nimbits.client.model.email.EmailAddress;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/29/12
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientDao {

    void addClientCommunication(EmailAddress emailAddress,
                                String contactPhone,
                                String companyName,
                                String clientName,
                                String message,
                                CommunicationType type);
}
