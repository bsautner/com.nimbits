package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 4:43 PM
 */
public class ValueApiTest extends NimbitsServletTest {


    ValueApi impl;


    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();
        impl = new ValueApi();
    }

    @Test
    public void testPostValue() throws IOException, ServletException {
        req.removeAllParameters();
        req.setContentType("application/json");
        Value v = ValueFactory.createValueModel(2.345);
        req.addHeader("id", point.getKey());
        //req.addParameter("json", GsonFactory.getInstance().toJson(v));
        String json = GsonFactory.getInstance().toJson(v);
        req.setContent(json.getBytes());
        req.setMethod("POST");
        impl.doPost(req, resp);
        assertEquals(HttpServletResponse.SC_OK, resp.getStatus());
        List<Value> vr = ValueTransaction.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(vr.get(0), v);
        assertEquals(resp.getStatus(), 200);


    }
    @Test
    public void testPostBodyValue() throws IOException, ServletException {
        req.removeAllParameters();
        req.setContentType("application/json");
        Random r = new Random();
        Value v = ValueFactory.createValueModel(r.nextDouble());
        req.addHeader("id", point.getKey());
        //req.addParameter("json", GsonFactory.getInstance().toJson(v));
        String json = GsonFactory.getInstance().toJson(v);
        req.setContent(json.getBytes());
        req.setMethod("POST");
        impl.doPost(req, resp);

        List<Value> vr = ValueTransaction.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(vr.get(0).getDoubleValue(), v.getDoubleValue(), 0.001);
        assertEquals(resp.getStatus(), 200);


    }
    @Test
    public void testPostValueCummulative()     {

        point.setPointType(PointType.cumulative);
        point.setFilterType(FilterType.none);
        EntityServiceImpl.addUpdateEntity(Arrays.<Entity>asList(pointChild));
        Value v = ValueFactory.createValueModel(1);
        for (int i = 0; i < 3; i++) {
            ValueTransaction.recordValue(user, point, v);
            // Thread.sleep(1500);

        }
        List<Value> vr = ValueTransaction.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(3.0, vr.get(0).getDoubleValue());


    }
}
