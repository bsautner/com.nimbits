package com.nimbits.server.dao.value;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.time.TimespanServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 12:00 PM
 */
public class ValueDaoImplTest {
    private Point point;
    ValueDAOImpl dao;
    @Before
    public void setUp() {

        helper.setUp();
        point = PointModelFactory.createPointModel(UUID.randomUUID().toString());
        dao = new ValueDAOImpl(point);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    double total = 0.0;
    private  List<Value> loadSomeData() {
        List<Value> values = new ArrayList<Value>();
        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            Value v = ValueModelFactory.createValueModel(r.nextDouble());
            total += v.getDoubleValue();
            values.add(v);
        }
        return values;
    }

    @Test
    public void testConsolidateDate() throws NimbitsException {
        List<Value> values;
        Date zero = TimespanServiceFactory.getInstance().zeroOutDate(new Date());
        for (int i = 1; i < 11; i++) {
        values= new ArrayList<Value>();
        values.add(ValueModelFactory.createValueModel(1));
        values.add(ValueModelFactory.createValueModel(1));
        values.add(ValueModelFactory.createValueModel(1));
        dao.recordValues(values);
            assertEquals(i, dao.getAllStores().size());
        }


         dao.consolidateDate(zero);
        assertEquals(1, dao.getAllStores().size());

        List<Value> result = dao.getTopDataSeries(100);
        double total = 0.0;
        for (Value v : result) {
            total += v.getDoubleValue();

        }
        assertEquals(30.0, total, 0.0);



    }


    @Test
    public void testGetTopDataSeries(){
        List<Value> values = loadSomeDataOverDays();


        try {
            dao.recordValues(values);
            List<Value> result = dao.getTopDataSeries(10);
            assertEquals(10, result.size());
        } catch (NimbitsException e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    public void testGetRecordedValuePrecedingTimestamp() {
        List<Value> values = loadSomeDataOverDays();


        ValueDAOImpl dao = new ValueDAOImpl(point);
        try {
            dao.recordValues(values);

            for (int i = 0; i < 100; i++) {
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                Double d1 = (double) i;
                Value vx = dao.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
        } catch (NimbitsException e) {
            fail();
            e.printStackTrace();
        }

    }



    @Test
    public void testGetRecordedValuePrecedingTimestampMultiplePoints() {
        List<Value> values = loadSomeDataOverDays();
        Point point1 = PointModelFactory.createPointModel(UUID.randomUUID().toString());
        Point point2 = PointModelFactory.createPointModel(UUID.randomUUID().toString());
        Point point3 = PointModelFactory.createPointModel(UUID.randomUUID().toString());

        ValueDAOImpl dao1 = new ValueDAOImpl(point1);
        ValueDAOImpl dao2 = new ValueDAOImpl(point2);
        ValueDAOImpl dao3 = new ValueDAOImpl(point3);


        try {
            dao1.recordValues(values);
            dao2.recordValues(values);
            dao3.recordValues(values);

            for (int i = 0; i < 100; i++) {
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                Double d1 = (double) i;
                Value vx = dao1.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
            for (int i = 0; i < 100; i++) {
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                Double d1 = (double) i;
                Value vx = dao2.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
            for (int i = 0; i < 100; i++) {
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                Double d1 = (double) i;
                Value vx = dao3.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
        } catch (NimbitsException e) {
            fail();
            e.printStackTrace();
        }

    }


    private List<Value> loadSomeDataOverDays() {
        List<Value> values = new ArrayList<Value>();
        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, -1 * i);
            Double d1 = (double) i;
            Value v1 = ValueModelFactory.createValueModel(d1, c1.getTime());
            values.add(v1);
        }
        return values;
    }


    @Test
    public void testRecordValues() {

        List<Value> values = loadSomeData();

        ValueDAOImpl dao = new ValueDAOImpl(point);
        try {
            dao.recordValues(values);
            List<Value> result = dao.getTopDataSeries(100);
            assertNotNull(result);
            assertEquals(result.size(), 10);
            double ret = 0.0;
            for (Value v : result) {
                ret += v.getDoubleValue();
            }
            assertEquals(total, ret, 0.0);


        } catch (NimbitsException e) {
            e.printStackTrace();
            fail();
        }
    }



    // run this test twice to prove we're not leaking any state across tests
    private void doTest() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        assertEquals(0, ds.prepare(new Query("yam")).countEntities(withLimit(10)));
        ds.put(new Entity("yam"));
        ds.put(new Entity("yam"));
        assertEquals(2, ds.prepare(new Query("yam")).countEntities(withLimit(10)));
    }



    @Test
    public void testInsert1() {
        doTest();
    }

    @Test
    public void testInsert2() {
        doTest();
    }
    @Test
    public void testZeroOutDate() {
        Calendar now = Calendar.getInstance();
        Calendar midnightAm = Calendar.getInstance();
        midnightAm.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0);
        midnightAm.add(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND) * -1);
        Date zero = TimespanServiceFactory.getInstance().zeroOutDate(now.getTime());
        assertEquals(midnightAm.getTime(), zero);
    }

}
