package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.api.ValueApi;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created by benjamin on 8/8/13.
 */
public class ValueApiNotLoggedInTest extends NimbitsServletNotLoggedInTest {

    @Resource(name = "valueApi")
    ValueApi impl;

    @Test
    public void testPostValue() throws IOException {
        req.removeAllParameters();
        req.setContentType("application/json");
        Value v = ValueFactory.createValueModel(2.345);
        req.addParameter("email", email);
        //req.addParameter("json", GsonFactory.getInstance().toJson(v));
        String json = GsonFactory.getInstance().toJson(v);
        req.setContent(json.getBytes());
        req.setMethod("POST");
        impl.handleRequest(req, resp);


        assertEquals(resp.getStatus(), 200);


    }
}
