/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.transactions.value;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.files.*;
import com.google.common.collect.Range;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.time.TimespanService;
import com.nimbits.server.time.TimespanServiceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.*;
import java.util.zip.DataFormatException;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 12:00 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml",
        "classpath:META-INF/applicationContext-factory.xml"

})
public class ValueDaoImplTest extends NimbitsServletTest {




    double total = 0.0;
    private  List<Value> loadSomeData() {
        List<Value> values = new ArrayList<Value>();
        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            Value v = ValueFactory.createValueModel(r.nextDouble());
            total += v.getDoubleValue();
            values.add(v);
        }
        return values;
    }




    @Test
    public void testConsolidateDate() throws Exception {
        Date zero = TimespanService.zeroOutDateToStart(new Date());
        Point newPoint = createRandomPoint();
        for (int i = 1; i < 11; i++) {
            List<Value> values = new ArrayList<Value>(3);
            values.add(ValueFactory.createValueModel(1));
            values.add(ValueFactory.createValueModel(1));
            values.add(ValueFactory.createValueModel(1));
            valueDao.recordValues(newPoint, values);
            assertEquals(i, blobStore.getAllStores(newPoint).size());
        }


        List<Value> consolidated = blobStore.consolidateDate(newPoint, zero);
        valueDao.recordValues(newPoint, consolidated);
        assertEquals(1, blobStore.getAllStores(newPoint).size());

        List<Value> result = valueDao.getTopDataSeries(newPoint, 100);
        double total = 0.0;
        for (Value v : result) {
            total += v.getDoubleValue();

        }
        assertEquals(30.0, total, 0.0);

    }

    @Test
    public void testMissingBlobRecovery() throws Exception {
        Date zero = TimespanServiceFactory.getInstance().zeroOutDateToStart(new Date());
        String key = null;
        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        for (int i = 1; i < 11; i++) {
            List<Value> values = new ArrayList<Value>(3);
            values.add(ValueFactory.createValueModel(1));
            values.add(ValueFactory.createValueModel(1));
            values.add(ValueFactory.createValueModel(1));
            List<ValueBlobStore> d = valueDao.recordValues(point, values);
            assertFalse(d.isEmpty());
            assertEquals(i, blobStore.getAllStores(point).size());
            key = d.get(0).getBlobKey();
        }

        blobstoreService.delete(new BlobKey(key));

        List<Value> values = blobStore.consolidateDate(point, zero);
        valueService.recordValues(user, point, values);
        assertEquals(1, blobStore.getAllStores(point).size());

        List<Value> result = valueDao.getTopDataSeries(point, 100);
        double total = 0.0;
        for (Value v : result) {
            total += v.getDoubleValue();

        }
        assertEquals(27.0, total, 0.0);

    }

//    @Test
//    public void testGetBlobStoreByBlobKey()  {
//        Date zero = TimespanServiceFactory.getInstance().zeroOutDateToStart(new Date());
//        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
//
//        List<Value> values = new ArrayList<Value>(3);
//        values.add(ValueFactory.createValueModel(1));
//        values.add(ValueFactory.createValueModel(2));
//        values.add(ValueFactory.createValueModel(3));
//        List<ValueBlobStore> d = valueDao.recordValues(point, values);
//        assertFalse(d.isEmpty());
//
//        String key = d.get(0).getBlobKey();
//
//        List<ValueBlobStore> v =   valueDao.getBlobStoreByBlobKey(new BlobKey(key));
//        assertFalse(v.isEmpty());
//
//
//        EntityName name = CommonFactory.createName("f", EntityType.file);
//        com.nimbits.client.model.entity.Entity e = EntityModelFactory.createEntity(name, "", EntityType.file,
//                ProtectionLevel.everyone,user.getKey(), user.getKey() );
//
//
//
//    }
//

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
    public void testBlobStoreWithCompression() throws IOException, DataFormatException {
//        String sample =  "hello compression";
//        BlobKey dataBlobKey =
//                BlobStoreImpl.putInBlobStore("MULTIPART_FORM_DATA",
//                        CompressionImpl.compress(sample.getBytes()));
//        System.out.println(dataBlobKey);


    }


    @Test
    public void testGetTopDataSeries(){
        List<Value> values = loadSomeDataOverDays();


        try {

            valueDao.recordValues(point, values);
            List<Value> result = valueDao.getTopDataSeries(point, 10);
            assertEquals(10, result.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }
    @Test
    public void testStaticGetTopDataSeries(){
        List<Value> values = loadSomeDataOverDays();


        try {
            valueDao.recordValues(point, values);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -10);

            List<Value> result = valueService.getDataSegment(point, Range.closed(c.getTime(), new Date()), Range.closed(0, 10));
                       //valueDao.getTopDataSeries(point, 10);
            assertEquals(10, result.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    public void testExpireData() throws Exception {
        final List<Value> values = new ArrayList<Value>(90);
        for (int i = 10; i < 100; i++) {
            final Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, -1 * i);
            final Double d1 = (double) i;
            final Value v1 = ValueFactory.createValueModel(d1, c1.getTime());
            values.add(v1);
        }
        valueDao.recordValues(point, values);

        List<Value> result1 = valueDao.getTopDataSeries(point, 90);
        assertEquals(90, result1.size());

        final List<Value> values2 = new ArrayList<Value>(5);
        for (int i = 0; i < 5; i++) {
            final Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, -1 * i);
            final Double d1 = (double) i;
            final Value v1 = ValueFactory.createValueModel(d1, c1.getTime());
            values2.add(v1);
        }

        valueDao.recordValues(point, values2);
        blobStore.deleteExpiredData(point);


        List<Value> result = valueDao.getTopDataSeries(point, 10);
            assertEquals(5, result.size());
    }
    @Test
    public void testGetRecordedValuePrecedingTimestamp() {
        final List<Value> values = loadSomeDataOverDays();



        try {
            valueDao.recordValues(point, values);
            List<Value> all = valueDao.getTopDataSeries(point, 1000);
            assertEquals(100, all.size());
            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final List<Value> vx = valueDao.getRecordedValuePrecedingTimestamp(point, c1.getTime());
                assertEquals(d1, vx.get(0).getDoubleValue(), 0.0);
            }
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }

    }



    @Test
    public void testGetRecordedValuePrecedingTimestampMultiplePoints()  {
        final List<Value> values = loadSomeDataOverDays();
        EntityName name1 = CommonFactory.createName("1", EntityType.point);
        EntityName name2 = CommonFactory.createName("1", EntityType.point);
        EntityName name3 = CommonFactory.createName("1", EntityType.point);
        com.nimbits.client.model.entity.Entity entity1 = EntityModelFactory.createEntity(name1, "", EntityType.point, ProtectionLevel.everyone, "", "");
        com.nimbits.client.model.entity.Entity entity2 = EntityModelFactory.createEntity(name2, "", EntityType.point, ProtectionLevel.everyone, "", "");
        com.nimbits.client.model.entity.Entity entity3 = EntityModelFactory.createEntity(name3, "", EntityType.point, ProtectionLevel.everyone, "", "");


        final Point point1 =   PointModelFactory.createPointModel(
                entity1,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0 );
        final Point point2 =   PointModelFactory.createPointModel(
                entity2,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0 );
        final Point point3 =   PointModelFactory.createPointModel(
                entity3,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0 );



        try {
            valueDao.recordValues(point1, values);
            valueDao.recordValues(point2, values);
            valueDao.recordValues(point3, values);

            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final List<Value> vx = valueDao.getRecordedValuePrecedingTimestamp(point1, c1.getTime());
                assertEquals(d1, vx.get(0).getDoubleValue(), 0.0);
            }
            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final List<Value> vx = valueDao.getRecordedValuePrecedingTimestamp(point2, c1.getTime());
                assertEquals(d1, vx.get(0).getDoubleValue(), 0.0);
            }
            for (int i = 0; i < 100; i++) {
                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -1 * i);
                final Double d1 = (double) i;
                final List<Value> vx = valueDao.getRecordedValuePrecedingTimestamp(point3, c1.getTime());
                assertEquals(d1, vx.get(0).getDoubleValue(), 0.0);
            }
        } catch (Exception e) {
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
            final Value v1 = ValueFactory.createValueModel(d1, c1.getTime());
            values.add(v1);
        }
        return values;
    }


    @Test
    public void testRecordValues() {

        List<Value> values = loadSomeData();


        try {
            valueDao.recordValues(point, values);
            List<Value> result = valueDao.getTopDataSeries(point, 100);
            assertNotNull(result);
            assertEquals(10, result.size());
            double ret = 0.0;
            for (Value v : result) {
                ret += v.getDoubleValue();
            }
            assertEquals(total, ret, 0.0);


        } catch (Exception e) {

            fail();
        }
    }

    @Test
    public void testRecordValuesLoad() {
        long s = new Date().getTime();

        for (int i = 0; i < 1000; i++) {
            List<Value> values = loadSomeData();


            try {
                valueDao.recordValues(point, values);
                List<Value> result = valueDao.getTopDataSeries(point, 100);
                assertNotNull(result);

                double ret = 0.0;
                for (Value v : result) {
                    ret += v.getDoubleValue();
                }

            } catch (Exception e) {

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
        Date zero = TimespanServiceFactory.getInstance().zeroOutDateToStart(now.getTime());
        assertEquals(midnightAm.getTime(), zero);
    }
    @Test
    public void testZeroOutDate2() {
        Calendar now = Calendar.getInstance();
        Calendar midnightAm = Calendar.getInstance();
        midnightAm.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0);
        midnightAm.add(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND) * -1);
        Date zero = TimespanServiceFactory.getInstance().zeroOutDateToStart(now.getTime());
        Date tonight = TimespanServiceFactory.getInstance().zeroOutDateToEnd(now.getTime());
        long diff =   tonight.getTime() - zero.getTime() ;
        assertEquals(86400000, diff); //ms in a day
        assertEquals(midnightAm.getTime(), zero);
    }



}
