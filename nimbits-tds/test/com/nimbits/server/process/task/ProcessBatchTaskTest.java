package com.nimbits.server.process.task;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.transactions.service.value.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 8:54 AM
 */
public class ProcessBatchTaskTest extends NimbitsServletTest {
    private static final double DELTA = 0.001;
    private static final double COMPRESSION_VALUE_1 = 0.1;
    ProcessBatchTask servlet = new ProcessBatchTask();
    Random r = new Random();
    @Test
    public void processBatchTestNormal() throws NimbitsException {
        addAuth();
        double v1 = r.nextDouble();
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));
        double v2 = r.nextDouble();
        req.addParameter("p2", pointChildName.getValue());
        req.addParameter("v2", String.valueOf(v2));
        servlet.doPost(req, resp);
        Value rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);
        Value rv2 = ValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertNotNull(rv1);
        assertEquals(rv1.getDoubleValue(), v1, DELTA);
        assertNotNull(rv2);
        assertEquals(rv2.getDoubleValue(), v2, DELTA);
    }

    private void addAuth() {
        String userJson = GsonFactory.getInstance().toJson(user);
        req.addParameter(Parameters.pointUser.getText(), userJson);
        String keyJson = GsonFactory.getInstance().toJson(user.getAccessKeys());
        req.addParameter(Parameters.key.getText(), keyJson);
    }

    @Test
    public void processBatchTestCompression() throws NimbitsException {

        addAuth();
        point.setFilterValue(COMPRESSION_VALUE_1);


        double v1 = r.nextDouble();
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));



        servlet.doPost(req, resp);

        Value rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);

        assertNotNull(rv1);
        assertEquals(rv1.getDoubleValue(), v1, DELTA);


    }


    @Test
    public void TestCompressionWithBatch() throws NimbitsException, InterruptedException {
        addAuth();
        point.setFilterValue(2.0);
        EntityServiceFactory.getInstance().addUpdateEntity(point);
        Point r = (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), EntityType.point).get(0);
        assertNotNull(r);
        assertEquals(2.0,r.getFilterValue(),  DELTA);



        for (int i = 0; i < 40; i++) {
            req.addParameter("p"+i, pointName.getValue());
            req.addParameter("v"+i, String.valueOf(i));
            req.addParameter("t"+i, String.valueOf(new Date().getTime()));

            Thread.sleep(100);

        }
        //  System.out.println(b.toString());
        servlet.doPost(req, resp);

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
    public void testCompressionWithBatchWithMissingPoints() throws NimbitsException, InterruptedException {

        addAuth();
        point.setFilterValue(2.0);
        EntityServiceFactory.getInstance().addUpdateEntity(point);
        Point r = (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), EntityType.point).get(0);
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
        servlet.doPost(req, resp);

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
    public void processBatchTestBadNames() throws NimbitsException {
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



        servlet.doPost(req, resp);

        Value rv1 = ValueServiceFactory.getInstance().getCurrentValue(point);
        Value rv2 = ValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertNotNull(rv1);
        assertEquals(rv1.getDoubleValue(), v1, DELTA);

        assertNotNull(rv2);
        assertEquals(rv2.getDoubleValue(), v2, DELTA);
    }

    @Test
    public void processBatchNoParamsValidUser() throws NimbitsException {
        addAuth();
        servlet.doPost(req, resp);

    }
}
