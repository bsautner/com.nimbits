package com.nimbits.server.service.client;

import com.nimbits.client.enums.client.CommunicationType;
import com.nimbits.client.model.email.EmailAddress;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/29/12
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientService {

    void addClientCommunication(EmailAddress emailAddress,
                                String contactPhone,
                                String companyName,
                                String clientName,
                                String message,
                                CommunicationType type);
}
