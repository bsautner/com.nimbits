package com.nimbits.server.cron;

import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:24 PM
 */
public class QuotaResetCronTest extends NimbitsServletTest {


    @Test
    public void testQuotaReset() throws IOException {

        QuotaResetCron cron = new QuotaResetCron();
        cron.doGet(null, null);
        ShardedCounter counter = CounterFactory.getHelper().getOrCreateCounter(email);
        int count;
        for (int i = 0; i < 100; i++) {
        counter.increment();
       }
        CounterFactory f = new CounterFactory();
        count =  f.getCounter(email).getCount();
        assertEquals(100, count);

        cron.doGet(req, resp);

        assertEquals(0, f.getCounter(email).getCount());


    }

}
