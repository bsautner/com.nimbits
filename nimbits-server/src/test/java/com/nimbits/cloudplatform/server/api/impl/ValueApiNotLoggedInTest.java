package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.api.ValueApi;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
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



    @Test
    public void testPostValue() throws IOException {

        UserTransaction.createUserRecord(CommonFactory.createEmailAddress("test@example.com"));

        ValueApi impl = new ValueApi();
        req.removeAllParameters();
        req.setContentType("application/json");
        Value v = ValueFactory.createValueModel(2.345);
        req.addParameter("email", email);
        //req.addParameter("json", GsonFactory.getInstance().toJson(v));
        String json = GsonFactory.getInstance().toJson(v);
        req.setContent(json.getBytes());
        req.setMethod("POST");
        impl.doPost(req, resp);


        assertEquals(resp.getStatus(), 401);


    }
}
