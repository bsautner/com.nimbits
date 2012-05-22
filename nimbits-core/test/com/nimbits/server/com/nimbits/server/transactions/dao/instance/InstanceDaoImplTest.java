package com.nimbits.server.com.nimbits.server.transactions.dao.instance;

import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModelFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 4:14 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class InstanceDaoImplTest {

    private InstanceTransactions instanceTransactions;

    @Resource(name="instanceDao")
    public void setInstanceTransactions(InstanceTransactions transactions) {
        this.instanceTransactions = transactions;
    }



    @Test
    public void testAddUpdateInstance() throws Exception {
        Instance s = InstanceModelFactory.createInstance("I don't exist", CommonFactoryLocator.getInstance().createEmailAddress("b@b.com"), "-");
        Instance r = instanceTransactions.addUpdateInstance(s);
        assertNotNull(r);

    }

    @Test
    public void testAddInstance() throws Exception {
        Instance s = InstanceModelFactory.createInstance("I don't exist", CommonFactoryLocator.getInstance().createEmailAddress("b@b.com"), "-");
        Instance r = instanceTransactions.addUpdateInstance(s);
        assertNotNull(r);
    }

    @Test
    public void testUpdateInstance() throws Exception {

    }

    @Test
    public void testDeleteInstance() throws Exception {

    }

    @Test
    public void testReadInstance() throws Exception {

    }
}
