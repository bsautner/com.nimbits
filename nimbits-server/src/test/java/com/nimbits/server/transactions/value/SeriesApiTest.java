package com.nimbits.server.transactions.value;

import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


/**
 * Author: Benjamin Sautner
 * Date: 12/31/12
 * Time: 1:26 PM
 */
public class SeriesApiTest extends NimbitsServletTest {


    @Test
    public void testGetApi() throws Exception {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -10);
        req.addParameter(Parameters.sd.name(), String.valueOf(c.getTimeInMillis()));
        req.addParameter(Parameters.ed.name(), String.valueOf(new Date().getTime()));
        req.addParameter(Parameters.id.name(), point.getKey());

        Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();


        List<Value> values = loadSomeDataOverDays();
        valueDao.recordValues(point, values);

        seriesApi.doGet(req, resp);
        assertEquals(200, resp.getStatus());
        String response = resp.getContentAsString();
        List<Value> v = GsonFactory.getInstance().fromJson(response, valueListType);
        assertFalse(v.isEmpty());
    }
    @Test
    public void testPerformance() throws Exception {
        long s = System.currentTimeMillis();
        req.addParameter(Parameters.id.name(), point.getKey());

        Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();


        List<Value> values = load1000();
        valueDao.recordValues(point, values);
        System.out.println("recorded 1000 values in: " + (System.currentTimeMillis() - s));
        s = System.currentTimeMillis();
        seriesApi.doGet(req, resp);
        assertEquals(200, resp.getStatus());
        String response = resp.getContentAsString();
        System.out.println("Got 1000 values in: " + (System.currentTimeMillis() - s));
        List<Value> v = GsonFactory.getInstance().fromJson(response, valueListType);
        assertFalse(v.isEmpty());
        assertEquals(v.size(), 1000);

        s = System.currentTimeMillis();
        MockHttpServletResponse resp2 = new MockHttpServletResponse();;
        seriesApi.doGet(req, resp2);
        assertEquals(200, resp2.getStatus());
        String response2 = resp2.getContentAsString();
        System.out.println("Got 1000 values again in: " + (System.currentTimeMillis() - s));
        List<Value> v2 = GsonFactory.getInstance().fromJson(response2, valueListType);
        assertFalse(v2.isEmpty());
        assertEquals(v2.size(), 1000);

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
    private static List<Value> load1000() {
        final List<Value> values = new ArrayList<Value>(1000);
        final Calendar c1 = Calendar.getInstance();
        for (int i = 0; i < 1000; i++) {

            c1.add(Calendar.DATE, -1 * i);
            final Double d1 = (double) i;
            final Value v1 = ValueFactory.createValueModel(d1, c1.getTime());
            values.add(v1);
        }
        return values;
    }

}
