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

package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class PointMaintTaskTest extends NimbitsServletTest {


    @Resource(name="pointTask")
    PointMaintTask pointTask;

    @Resource(name="valueService")
    ValueService valueService;


    @Resource(name="entityService")
    EntityService entityService;

    private static final double DELTA = .001;

    @Test
    public void testGet() throws NimbitsException {

        final Map<String,Entity> e = entityService.getSystemWideEntityMap(user, EntityType.point);
        assertTrue(!e.isEmpty());

        for (final Entity en : e.values()) {
            final String j = GsonFactory.getInstance().toJson(en);
            req.setParameter(Parameters.json.getText(), j);
            assertNotNull(req.getParameter(Parameters.json.getText()));
            pointTask.processPost(req);

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
          valueService.recordValues(user, point, values);
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

        pointTask.consolidateBlobs(point);

        Iterator<BlobInfo> iterator2 = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator2.hasNext());
        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2 ++;

        }
//        assertEquals(1, count2);  //prove all data was consolidated into one file

        List<Value> fResults = valueService.getTopDataSeries(point, runs);
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
            valueService.recordValues(user, point, values);
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

            valueService.recordValues(user, point, values);
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

        pointTask.consolidateBlobs(point);

        Iterator<BlobInfo> iterator2 = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator2.hasNext());
        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2 ++;

        }
//        assertEquals(2, count2);  //prove all data was consolidated into one file
        Timespan ts = TimespanModelFactory.createTimespan(sd, c.getTime());
        List<Value> fResults = valueService.getDataSegment(point, ts);
        assertEquals(runs << 1, fResults.size());
        double result = 0.0;

        for (Value vx : fResults) {
            result += vx.getDoubleValue();
        }
        assertEquals(sum, result, DELTA);    //proves no data was lost

        List<ValueBlobStore> stores = valueService.getAllStores(point);



        valueService.mergeTimespan(point, ts);

        Iterator<BlobInfo> iterator4 = new BlobInfoFactory().queryBlobInfos();
        assertTrue(iterator4.hasNext());
        int count4 = 0;
        while (iterator4.hasNext()) {
            iterator4.next();
            count4 ++;

        }
//        assertEquals(1, count4);  //prove all data was consolidated into one file
        List<Value> postResults = valueService.getDataSegment(point, ts);
        double ss = 0;
        for (Value p : postResults) {
                  ss+= p.getDoubleValue();
        }
        assertEquals(sum, ss,DELTA);
        List<Value> current =valueService.getCurrentValue(point);
        assertEquals(c.getTime().getTime(), current.get(0).getTimestamp().getTime());
        assertEquals(mostRecent, current.get(0).getDoubleValue(), DELTA);
    }

}
