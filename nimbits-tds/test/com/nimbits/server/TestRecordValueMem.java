package com.nimbits.server;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.recordedvalue.RecordedValueTransactionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 12:33 PM
 */
public class TestRecordValueMem {
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private final LocalServiceTestHelper task =
            new LocalServiceTestHelper(new LocalTaskQueueTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        //  task.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        // task.tearDown();
    }

    @Test
    public void testMem1() throws NimbitsException, InterruptedException {

        Point p = PointModelFactory.createPointModel(0, 0);
        EntityName name = (CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString()));
        long ts = new Date().getTime();

        Random r = new Random();
        List<Long> used = new ArrayList<Long>();
        used.add(0L);
        long mostRecent = 0;
        for (int i = -100; i < 0; i++) {
            long randomTs = 0;

            while (used.contains(randomTs)) {
                randomTs = ts - r.nextInt(1000) * 10;
            }
            if (randomTs > mostRecent) {
                mostRecent = randomTs;
            }
            used.add(randomTs);
            Value v = ValueModelFactory.createValueModel(r.nextDouble(), new Date(randomTs));
            RecordedValueTransactionFactory.getInstance(p).recordValue(v);
        }


        List<Value> rx = RecordedValueTransactionFactory.getInstance(p).getCache();
        assertEquals(100, rx.size());
        long last = new Date().getTime();
        for (Value vx : rx) {

            assertTrue(vx.getTimestamp().getTime() < last);
            last = vx.getTimestamp().getTime();

        }

        assertTrue(mostRecent == rx.get(0).getTimestamp().getTime());
        Value rv = RecordedValueTransactionFactory.getInstance(p).getRecordedValuePrecedingTimestamp(new Date());
        assertTrue(mostRecent == rv.getTimestamp().getTime());

        List<Value> top = RecordedValueTransactionFactory.getInstance(p).getTopDataSeries(100);
        assertEquals(100, top.size());


        RecordedValueTransactionFactory.getInstance(p).moveValuesFromCacheToStore();

        List<Value> rx1 = RecordedValueTransactionFactory.getInstance(p).getCache();
        assertEquals(0, rx1.size());

    }

}
