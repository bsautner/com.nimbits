package com.nimbits.server.api.impl;

import com.google.gson.reflect.TypeToken;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static junit.framework.Assert.assertEquals;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 4:43 PM
 */
public class BatchApiTest extends NimbitsServletTest {

    public static final int EXPECTED = 5;


    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
        super.setup();
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

        Type listType = new TypeToken<Map<String, List<ValueModel>>>() {
        }.getType();

        String json = GsonFactory.getInstance().toJson(map, listType);
        req.setContent(json.getBytes());
        req.setMethod("POST");

        batchApi.doPost(req, resp);

        List<Value> vx = valueService.getTopDataSeries(point, 5);
        assertEquals(EXPECTED, vx.size());

        double r1 = 0;
        double r2 = 0;

        for (Value value : vx) {
            r1 += value.getDoubleValue();
        }
        assertEquals(c1, r1, .001);

        List<Value> vx2 = valueService.getTopDataSeries(pointChild, 5);
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
