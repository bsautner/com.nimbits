package com.nimbits.server.dao.value;

import com.google.appengine.api.datastore.*;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.*;
import com.google.appengine.tools.development.testing.*;

import com.nimbits.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.time.*;
import com.nimbits.server.value.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 12:00 PM
 */
public class ValueDaoImplTest {
    private Point point;

    @Before
    public void setUp() {

        helper.setUp();
        point = PointModelFactory.createPointModel(UUID.randomUUID().toString());
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
            total += v.getNumberValue();
            values.add(v);
        }
        return values;
    }

    @Test
    public void testGetTopDataSeries(){
        List<Value> values = loadSomeDataOverDays();
        ValueDAOImpl dao = new ValueDAOImpl(point);

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
                assertEquals(d1, vx.getNumberValue(), 0.0);
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
                ret += v.getNumberValue();
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
