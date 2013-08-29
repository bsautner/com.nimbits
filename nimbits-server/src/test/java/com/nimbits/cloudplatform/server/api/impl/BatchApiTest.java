package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.api.BatchApi;
import com.nimbits.cloudplatform.server.api.ValueApi;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 4:43 PM
 */
public class BatchApiTest extends NimbitsServletTest {

    public static final int EXPECTED = 5;

    BatchApi impl = new BatchApi();


    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();
    }

    @Test
    public void testPostValue() throws InterruptedException, ServletException, IOException {
        req.removeAllParameters();
        req.setContentType("application/json");
        Random r = new Random();

        Map<String, List<Value>> map = new HashMap<String, List<Value>>(2);

        double c1 = 0;
        double c2 = 0;

        List<Value> list = new ArrayList<Value>();
        for (int i = 0; i < EXPECTED; i++) {
            double vx = r.nextDouble() * 100;
            c1 += vx;
            Value v = ValueFactory.createValueModel(vx);
            list.add(v);
            Thread.sleep(100);

        }
        map.put(point.getKey(), list);
        List<Value> list2 = new ArrayList<Value>();

        for (int i = 0; i < EXPECTED; i++) {
            double vx = r.nextDouble() * 100;
            c2 += vx;
            Value v = ValueFactory.createValueModel(vx);
            list2.add(v);
            Thread.sleep(100);
        }
        map.put(pointChild.getKey(), list2);

        req.addParameter("id", point.getKey());
        //req.addParameter("json", GsonFactory.getInstance().toJson(v));
        String json = GsonFactory.getInstance().toJson(map);
        req.setContent(json.getBytes());
        req.setMethod("POST");
        impl.doPost(req, resp);

        List<Value> vx = ValueTransaction.getTopDataSeries(point, 5);
        assertEquals(EXPECTED, vx.size());

        double r1 = 0;
        double r2 = 0;

        for (Value value : vx) {
            r1 += value.getDoubleValue();
        }
        assertEquals(c1, r1, .001);

        List<Value> vx2 = ValueTransaction.getTopDataSeries(pointChild, 5);
        assertEquals(EXPECTED, vx2.size());

        for (Value value : vx2) {
            r2 += value.getDoubleValue();
        }
        assertEquals(c2, r2, .001);



//        assertFalse(vr.isEmpty());
//        assertEquals(vr.get(0), v);
//        assertEquals(resp.getStatus(), 200);


    }


}
