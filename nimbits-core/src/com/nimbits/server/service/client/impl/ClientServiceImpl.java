package com.nimbits.server.service.client.impl;

import com.nimbits.client.enums.client.CommunicationType;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.service.client.ClientService;
import com.nimbits.server.transactions.dao.client.ClientDao;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/29/12
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional
@Service("clientService")
public class ClientServiceImpl implements ClientService {

    @Resource(name="clientDao")
    private ClientDao clientDao;

    @Override
    public void addClientCommunication(final EmailAddress emailAddress,
                                       final String contactPhone,
                                       final String companyName,
                                       final String clientName,
                                       final String message,
    final CommunicationType type) {
       clientDao.addClientCommunication(emailAddress, contactPhone, companyName, clientName, message, type);
    }
}
