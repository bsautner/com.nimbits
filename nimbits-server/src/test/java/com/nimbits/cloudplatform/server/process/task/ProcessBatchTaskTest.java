/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.process.task;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.*;


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
public class ProcessBatchTaskTest extends NimbitsServletTest {
    private static final double DELTA = 0.001;
    private static final double COMPRESSION_VALUE_1 = 0.1;

    Random r = new Random();



    @Resource(name="batchTask")
    ProcessBatchTask batchTask;

    @Test
    public void processBatchTestNormal() throws Exception {
        addAuth();
        double v1 = r.nextDouble();
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));
        double v2 = r.nextDouble();
        req.addParameter("p2", pointChildName.getValue());
        req.addParameter("v2", String.valueOf(v2));
        batchTask.handleRequest(req, resp);
        Thread.sleep(1000);
        List<Value> rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);
        List<Value> rv2 = ValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertNotNull(rv1);
        assertEquals(rv1.get(0).getDoubleValue(), v1, DELTA);
        assertNotNull(rv2);
        assertEquals(rv2.get(0).getDoubleValue(), v2, DELTA);
    }

    @Test
    public void processBatchTestGPS() throws Exception {
        addAuth();
        double v1 = r.nextDouble();
        double lt = 38.68551;
        double ln = -104.238281;
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));
        req.addParameter("lt1",String.valueOf(lt));
        req.addParameter("ln1",String.valueOf(ln));
        double v2 = r.nextDouble();
        req.addParameter("p2", pointChildName.getValue());
        req.addParameter("v2", String.valueOf(v2));
        req.addParameter("lt2",String.valueOf(lt));
        req.addParameter("ln2",String.valueOf(ln));

        batchTask.handleRequest(req, resp);
        List<Value> rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);
        List<Value> rv2 = ValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertNotNull(rv1);
        assertEquals(rv1.get(0).getDoubleValue(), v1, DELTA);
        assertEquals(lt, rv1.get(0).getLocation().getLat(), DELTA);
        assertEquals(ln, rv1.get(0).getLocation().getLng(), DELTA);
        assertNotNull(rv2);
        assertEquals(rv2.get(0).getDoubleValue(), v2, DELTA);
    }

    @Test
    public void processJsonBatchPost() throws Exception {
        addAuth();
        List<Value> values = new ArrayList<Value>(10);
        Random r = new Random();
        Calendar c = Calendar.getInstance();
        int runs = 1000;

        double total = 0.0;
        for (int i = 0; i < runs; i++) {
            double newValue = r.nextDouble();
            Value v = ValueFactory.createValueModel(newValue, c.getTime());
            total += newValue;
            c.add(Calendar.MINUTE, -1);
            values.add(v);






        }
        String json = GsonFactory.getInstance().toJson(values);
        req.addParameter("p1", pointName.getValue());
        req.addParameter("j1", json);
        batchTask.handleRequest(req, resp);
        List<Value> v = ValueServiceFactory.getInstance().getTopDataSeries(point, runs);
        assertEquals(runs, v.size());
        double newTotal = 0.0;
        for (Value vx : v) {
            newTotal += vx.getDoubleValue();

        }
        assertEquals(total, newTotal, DELTA);

    }

    private void addAuth() {
        String userJson = GsonFactory.getInstance().toJson(user);
        req.addParameter(Parameters.pointUser.getText(), userJson);
        String keyJson = GsonFactory.getInstance().toJson(user.getAccessKeys());
        req.addParameter(Parameters.key.getText(), keyJson);
    }

    @Test
    public void processBatchTestCompression() throws Exception {

        addAuth();
        point.setFilterValue(COMPRESSION_VALUE_1);


        double v1 = r.nextDouble();
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));



        batchTask.handleRequest(req, resp);

        List<Value> rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);

        assertNotNull(rv1);
        assertEquals(rv1.get(0).getDoubleValue(), v1, DELTA);


    }


    @Test
    public void TestCompressionWithBatch() throws Exception {
        addAuth();
        point.setFilterValue(2.0);
        EntityServiceFactory.getInstance().addUpdateEntity(Arrays.<Entity>asList(point));
        Point r = (Point) EntityServiceFactory.getInstance().getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertNotNull(r);
        assertEquals(2.0,r.getFilterValue(),  DELTA);



        for (int i = 0; i < 40; i++) {
            req.addParameter("p"+i, pointName.getValue());
            req.addParameter("v"+i, String.valueOf(i));
            req.addParameter("t"+i, String.valueOf(new Date().getTime()));

            Thread.sleep(100);

        }
        //  System.out.println(b.toString());
        batchTask.handleRequest(req, resp);

        double retVal = 0.0;

        Thread.sleep(100);
        List<Value> v = ValueServiceFactory.getInstance().getTopDataSeries(point, 10);// ClientHelper.client().getSeries(name, 10);
        assertFalse(v.isEmpty());
        for (Value x : v) {
            retVal += x.getDoubleValue();


        }
        assertEquals(255.0, retVal, DELTA);


    }

    @Test
    public void testCompressionWithBatchWithMissingPoints() throws Exception {

        addAuth();
        point.setFilterValue(2.0);
        EntityServiceFactory.getInstance().addUpdateEntity(Arrays.<Entity>asList(point));
        Point r = (Point) EntityServiceFactory.getInstance().getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertNotNull(r);
        assertEquals(2.0,r.getFilterValue(),  DELTA);



        for (int i = 0; i < 40; i++) {
            req.addParameter("p"+i, pointName.getValue());
            req.addParameter("v"+i, String.valueOf(i));
            req.addParameter("t"+i, String.valueOf(new Date().getTime()));

            Thread.sleep(100);



        }

        req.addParameter("p41","I_DO_NOT_EXIST");
        req.addParameter("v41", "FOO");
        req.addParameter("t41", String.valueOf(new Date().getTime()));
        batchTask.handleRequest(req, resp);

        double retVal = 0.0;

        Thread.sleep(100);
        List<Value> v = ValueServiceFactory.getInstance().getTopDataSeries(point, 10);// ClientHelper.client().getSeries(name, 10);
        assertFalse(v.isEmpty());
        for (Value x : v) {
            retVal += x.getDoubleValue();


        }
        assertEquals(255.0, retVal, DELTA);




    }
    @Test
    public void processBatchTestBadNames() throws Exception {
        addAuth();
        double v1 = r.nextDouble();
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));

        double v2 = r.nextDouble();
        req.addParameter("p3", pointChildName.getValue());
        req.addParameter("v3", String.valueOf(v2));

        double v3 = r.nextDouble();
        req.addParameter("p2", "I_DO_NOT_EXIST");
        req.addParameter("v2", String.valueOf(v2));



        batchTask.handleRequest(req, resp);

        List<Value> rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);
        List<Value> rv2 = ValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertNotNull(rv1);
        assertEquals(rv1.get(0).getDoubleValue(), v1, DELTA);

        assertNotNull(rv2);
        assertEquals(rv2.get(0).getDoubleValue(), v2, DELTA);
    }

    @Test
    public void processBatchNoParamsValidUser()  {
        addAuth();
        batchTask.handleRequest(req, resp);

    }
}
