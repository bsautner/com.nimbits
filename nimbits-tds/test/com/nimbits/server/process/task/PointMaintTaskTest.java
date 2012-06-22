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

package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.transactions.service.value.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/7/12
 * Time: 9:28 AM
 */
public class PointMaintTaskTest extends NimbitsServletTest {


    private static final double DELTA = .001;

    @Test
    public void testGet() throws NimbitsException {

        final Map<String,Entity> e = EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(EntityType.point);
        assertTrue(!e.isEmpty());

        for (final Entity en : e.values()) {
            final String j = GsonFactory.getInstance().toJson(en);
            req.setParameter(Parameters.json.getText(), j);
            assertNotNull(req.getParameter(Parameters.json.getText()));
            PointMaintTask.processPost(req);

        }

    }

    @Test
    public void testTimeBug() throws InterruptedException {
        long sample = 0;
        for (int i = 0; i < 10; i++) {
            Date d = new Date();
            assertFalse(d.getTime()==sample);
            sample = d.getTime();
            Thread.sleep(100);
        }

    }


    @Test
    public void testConsolidateBlobs() throws InterruptedException, NimbitsException {

      Random r = new Random();
      int t = 0;
      int runs = 100;
      double sum = 0;
        long lt = 0;
      for (int i = 0; i < runs; i++) {
          List<Value> values = new ArrayList<Value>(1);
          double v = r.nextDouble() * 100;
          Value mt = ValueFactory.createValueModel(v);
          values.add(mt);
          assertFalse(lt == mt.getTimestamp().getTime());

          lt = mt.getTimestamp().getTime();
          Thread.sleep(25);
          ValueTransactionFactory.getInstance(point).recordValues(values);
          sum += v;
      }
        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator.hasNext());
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count ++;

        }
        assertEquals(runs, count);  //prove a file was stored for each record

       PointMaintTask.consolidateBlobs(point);

        Iterator<BlobInfo> iterator2 = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator2.hasNext());
        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2 ++;

        }
//        assertEquals(1, count2);  //prove all data was consolidated into one file

        List<Value> fResults = ValueTransactionFactory.getInstance(point).getTopDataSeries(runs);
        assertEquals(runs, fResults.size());
        double result = 0.0;

        for (Value vx : fResults) {
            result += vx.getDoubleValue();
        }
        assertEquals(sum, result, DELTA);    //proves no data was lost



    }

    @Test
    public void testMergeDate() throws InterruptedException, NimbitsException {

        Random r = new Random();
        int t = 0;
        int runs = 100;
        double sum = 0;
        long lt = 0;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -2);
        Date sd = c.getTime();


        for (int i = 0; i < runs; i++) {
            List<Value> values = new ArrayList<Value>(1);
            double v = r.nextDouble() * 100;
            Value mt = ValueFactory.createValueModel(v, c.getTime());
            values.add(mt);
            assertFalse(lt == mt.getTimestamp().getTime());

            lt = mt.getTimestamp().getTime();
            c.add(Calendar.SECOND, 1);
            ValueTransactionFactory.getInstance(point).recordValues(values);
            sum += v;
        }

        c.add(Calendar.DATE, 1);
        double mostRecent = 0.0;
        for (int i = 0; i < runs; i++) {
            List<Value> values = new ArrayList<Value>(1);
            double v = r.nextDouble() * 100;
            c.add(Calendar.SECOND, 1);
            Value mt = ValueFactory.createValueModel(v, c.getTime());
            values.add(mt);
            assertFalse(lt == mt.getTimestamp().getTime());

            lt = mt.getTimestamp().getTime();

            ValueTransactionFactory.getInstance(point).recordValues(values);
            sum += v;
            mostRecent = v;
        }


        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator.hasNext());
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count ++;

        }
        assertEquals(runs << 1, count);  //prove a file was stored for each record

          PointMaintTask.consolidateBlobs(point);

        Iterator<BlobInfo> iterator2 = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator2.hasNext());
        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2 ++;

        }
//        assertEquals(2, count2);  //prove all data was consolidated into one file
        Timespan ts = TimespanModelFactory.createTimespan(sd, c.getTime());
        List<Value> fResults = ValueTransactionFactory.getInstance(point).getDataSegment(ts);
        assertEquals(runs << 1, fResults.size());
        double result = 0.0;

        for (Value vx : fResults) {
            result += vx.getDoubleValue();
        }
        assertEquals(sum, result, DELTA);    //proves no data was lost

        List<ValueBlobStore> stores = ValueTransactionFactory.getInstance(point).getAllStores();



        ValueTransactionFactory.getInstance(point).mergeTimespan(ts);

        Iterator<BlobInfo> iterator4 = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator4.hasNext());
        int count4 = 0;
        while (iterator4.hasNext()) {
            iterator4.next();
            count4 ++;

        }
//        assertEquals(1, count4);  //prove all data was consolidated into one file
        List<Value> postResults = ValueTransactionFactory.getInstance(point).getDataSegment(ts);
        double ss = 0;
        for (Value p : postResults) {
                  ss+= p.getDoubleValue();
        }
        assertEquals(sum, ss,DELTA);
        List<Value> current = ValueServiceFactory.getInstance().getCurrentValue(point);
        assertEquals(c.getTime().getTime(), current.get(0).getTimestamp().getTime());
        assertEquals(mostRecent, current.get(0).getDoubleValue(), DELTA);
    }

}
