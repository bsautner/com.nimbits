package com.nimbits.server.api.impl;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.client.exception.ValueException;
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
    public void testPostValueCummulative() throws ValueException {

        point.setPointType(PointType.cumulative);
        point.setFilterType(FilterType.none);
        EntityServiceFactory.getInstance(engine).addUpdateEntity(Arrays.<Entity>asList(pointChild));
        Value v = ValueFactory.createValueModel(1);
        for (int i = 0; i < 3; i++) {
            valueService.recordValue(req, user, point, v, false);
            // Thread.sleep(1500);

        }
        List<Value> vr = valueService.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(3.0, vr.get(0).getDoubleValue());


    }
}
