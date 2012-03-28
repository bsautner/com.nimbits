package com.nimbits.server.cron;

import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.dao.user.*;
import com.nimbits.server.dao.value.*;
import com.nimbits.server.user.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:24 PM
 */
public class QuotaResetCronTest {

    UserTransactions dao;
    @Before
    public void setUp() throws NimbitsException {

        helper.setUp();

        dao = UserTransactionFactory.getDAOInstance();
        dao.createNimbitsUser(CommonFactoryLocator.getInstance().createEmailAddress("test@example.com"));
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Test
    public void testQuotaReset() throws IOException {

        QuotaResetCron cron = new QuotaResetCron();
        cron.doGet(null, null);
        ShardedCounter counter = CounterFactory.getHelper().getOrCreateCounter("test@example.com");
        int count;
        for (int i = 0; i < 100; i++) {
        counter.increment();
       }
        CounterFactory f = new CounterFactory();
        count =  f.getCounter("test@example.com").getCount();
        assertEquals(100, count);

        cron.doGet(null, null);

        assertEquals(0, f.getCounter("test@example.com").getCount());


    }

}
