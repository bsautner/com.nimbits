package com.nimbits.server.dao.server;


import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
 public class ServerDaoImplTest  {

    private ServerTransactions serverTransactions;

    @Resource(name="serverDao")
    public void setServerTransactions(ServerTransactions transactions) {
        this.serverTransactions = transactions;
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    @Rollback(false)
    public void testAddUpdateServer() throws Exception {
       Server s = ServerModelFactory.createServer("speringtest29905", CommonFactoryLocator.getInstance().createEmailAddress("b@b.com"), "-");
       Server r = serverTransactions.addUpdateServer(s);
       assertNotNull(r);

    }

    @Test
    public void testAddServer() throws Exception {

    }

    @Test
    public void testUpdateServer() throws Exception {

    }

    @Test
    public void testDeleteServer() throws Exception {

    }

    @Test
    public void testReadServer() throws Exception {

    }
}
