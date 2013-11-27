package com.nimbits.server.api.impl;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Author: Benjamin Sautner
 * Date: 1/14/13
 * Time: 4:43 PM
 */
public class ValueApiTest extends NimbitsServletTest {


    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
        super.setup();
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();

    }

    @Test
    public void testPostValue() throws IOException, ServletException {
        req.removeAllParameters();
        req.setContentType("application/json");
        Value v = ValueFactory.createValueModel(2.345);
        req.addParameter("id", point.getKey());
        req.addParameter("json", GsonFactory.getInstance().toJson(v));


        req.setMethod("POST");
        valueApi.doPost(req, resp);
        assertEquals(HttpServletResponse.SC_OK, resp.getStatus());
        List<Value> vr = valueService.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(vr.get(0), v);
        assertEquals(resp.getStatus(), 200);


    }

    @Test
    public void testPostValueCummulative() {

        point.setPointType(PointType.cumulative);
        point.setFilterType(FilterType.none);
        EntityServiceFactory.getInstance(engine).addUpdateEntity(Arrays.<Entity>asList(pointChild));
        Value v = ValueFactory.createValueModel(1);
        for (int i = 0; i < 3; i++) {
            valueService.recordValue(req, user, point, v);
            // Thread.sleep(1500);

        }
        List<Value> vr = valueService.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(3.0, vr.get(0).getDoubleValue());


    }
}
