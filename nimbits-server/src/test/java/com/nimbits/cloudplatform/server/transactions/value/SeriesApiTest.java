package com.nimbits.cloudplatform.server.transactions.value;

import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.api.SeriesApi;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import org.junit.Test;

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


    SeriesApi api = new SeriesApi();


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

        api.doGet(req, resp);
        assertEquals(200, resp.getStatus());
        String response = resp.getContentAsString();
        List<Value> v = GsonFactory.getInstance().fromJson(response, valueListType);
        assertFalse(v.isEmpty());
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
}
