/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.dao.value;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Entity;
import static com.google.appengine.api.datastore.FetchOptions.Builder.*;
import com.google.appengine.api.files.*;
import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.time.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

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
    public void setUp() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("e", EntityType.point);

        com.nimbits.client.model.entity.Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, "", "");
        helper.setUp();

        point = PointModelFactory.createPointModel(entity);
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
    public void testBlobStore() throws IOException {
        // Get a file service
        FileService fileService = FileServiceFactory.getFileService();

        // Create a new Blob file with mime-type "text/plain"
        AppEngineFile file = fileService.createNewBlobFile("text/plain");

        // Open a channel to write to it
        boolean lock = false;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        out.println("The woods are lovely dark and deep.");
        out.println("But I have promises to keep.");

        // Close without finalizing and save the file path for writing later
        out.close();
        String path = file.getFullPath();

        // Write more to the file in a separate request:
        file = new AppEngineFile(path);

        // This time lock because we intend to finalize
        lock = true;
        writeChannel = fileService.openWriteChannel(file, lock);

        // This time we write to the channel directly
        writeChannel.write(ByteBuffer.wrap
                ("And miles to go before I sleep.".getBytes()));

        // Now finalize
        writeChannel.closeFinally();

        // Later, read from the file using the file API
        lock = false; // Let other people read at the same time
        FileReadChannel readChannel = fileService.openReadChannel(file, false);

        // Again, different standard Java ways of reading from the channel.
        BufferedReader reader =
                new BufferedReader(Channels.newReader(readChannel, "UTF8"));
        String line = reader.readLine();
        // line = "The woods are lovely dark and deep."

        readChannel.close();

        // Now read from the file using the Blobstore API
        BlobKey blobKey = fileService.getBlobKey(file);
        BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
        String segment = new String(blobStoreService.fetchData(blobKey, 30, 40));


        assertNotNull(blobKey);
        assertEquals(line, "The woods are lovely dark and deep." );
    }
    @Test
    public void testReadJson() {




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
        final List<Value> values = loadSomeDataOverDays();


        final ValueDAOImpl dao = new ValueDAOImpl(point);
        try {
            dao.recordValues(values);
            List<Value> all = dao.getTopDataSeries(1000);
            assertEquals(100, all.size());
            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final Value vx = dao.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
        } catch (NimbitsException e) {
            fail();
            e.printStackTrace();
        }

    }



    @Test
    public void testGetRecordedValuePrecedingTimestampMultiplePoints() throws NimbitsException {
        final List<Value> values = loadSomeDataOverDays();
        EntityName name1 = CommonFactoryLocator.getInstance().createName("1", EntityType.point);
        EntityName name2 = CommonFactoryLocator.getInstance().createName("1", EntityType.point);
        EntityName name3 = CommonFactoryLocator.getInstance().createName("1", EntityType.point);
        com.nimbits.client.model.entity.Entity entity1 = EntityModelFactory.createEntity(name1, "", EntityType.point, ProtectionLevel.everyone, "", "");
        com.nimbits.client.model.entity.Entity entity2 = EntityModelFactory.createEntity(name2, "", EntityType.point, ProtectionLevel.everyone, "", "");
        com.nimbits.client.model.entity.Entity entity3 = EntityModelFactory.createEntity(name3, "", EntityType.point, ProtectionLevel.everyone, "", "");


        final Point point1 = PointModelFactory.createPointModel(entity1);
        final Point point2 = PointModelFactory.createPointModel(entity2);
        final Point point3 = PointModelFactory.createPointModel(entity3);

        final ValueDAOImpl dao1 = new ValueDAOImpl(point1);
        final ValueDAOImpl dao2 = new ValueDAOImpl(point2);
        final ValueDAOImpl dao3 = new ValueDAOImpl(point3);


        try {
            dao1.recordValues(values);
            dao2.recordValues(values);
            dao3.recordValues(values);

            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final Value vx = dao1.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final Value vx = dao2.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final Value vx = dao3.getRecordedValuePrecedingTimestamp(c1.getTime());
                assertEquals(d1, vx.getDoubleValue(), 0.0);
            }
        } catch (NimbitsException e) {
            fail();
            e.printStackTrace();
        }

    }


    private static List<Value> loadSomeDataOverDays() {
        final List<Value> values = new ArrayList<Value>(100);
        for (int i = 0; i < 100; i++) {
            final Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, -1 * i);
            final Double d1 = (double) i;
            final Value v1 = ValueModelFactory.createValueModel(d1, c1.getTime());
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

            fail();
        }
    }

    @Test
    @Ignore
    public void testRecordValuesLoad() {
        long s = new Date().getTime();

        for (int i = 0; i < 1000; i++) {
            List<Value> values = loadSomeData();

            ValueDAOImpl dao = new ValueDAOImpl(point);
            try {
                dao.recordValues(values);
                List<Value> result = dao.getTopDataSeries(100);
                assertNotNull(result);

                double ret = 0.0;
                for (Value v : result) {
                    ret += v.getDoubleValue();
                }

            } catch (NimbitsException e) {

                fail();
            }
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
