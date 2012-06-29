package com.nimbits.server.transactions.dao.client.impl;

import com.nimbits.client.enums.client.CommunicationType;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.transactions.dao.client.ClientDao;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ClientDaoImplTest {

    private ClientDao clientDao;

    @Resource(name="clientDao")
    public void setInstanceTransactions(ClientDao dao) {
        this.clientDao = dao;
    }

    @Test
    public void testAddClientCommunication() throws Exception {
        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress("b@b.com");
        clientDao.addClientCommunication(emailAddress, "111", "company", "name", "hello world", CommunicationType.devRequest);
    }
}
