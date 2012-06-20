package com.nimbits.server.process.task;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.Parameters;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class CoreTaskTest extends NimbitsServletTest {

    @Test
    public void testPost() {
      CoreTask task = new CoreTask();
      req.removeAllParameters();
      String json = GsonFactory.getInstance().toJson(point);
      assertNotNull(json);
      req.addParameter(Parameters.entity.name(), json);
      req.addParameter(Parameters.action.name(), Action.update.getCode());
        req.addParameter(Parameters.instance.name(), "http://localhost");
      task.doPost(req, resp);
      int status = resp.getStatus();
      assertEquals(Const.HTTP_STATUS_OK, status );
      String response = resp.getHeader(Const.HTTP_HEADER_RESPONSE);
      System.out.println("RESPONSE: " + response);

    }

    @Test
    public void testLocationPost() {
        CoreTask task = new CoreTask();
        req.removeAllParameters();
        String json = GsonFactory.getInstance().toJson(point);
        assertNotNull(json);
        req.addParameter(Parameters.entity.name(), json);

        req.addParameter(Parameters.location.name(), "0,0");
        task.doPost(req, resp);
        int status = resp.getStatus();
        assertEquals(Const.HTTP_STATUS_OK, status );
        String response = resp.getHeader(Const.HTTP_HEADER_RESPONSE);
        System.out.println("RESPONSE: " + response);

    }

    @Test
    public void testBadPost() {
        CoreTask task = new CoreTask();
        req.removeAllParameters();
        String json = GsonFactory.getInstance().toJson(point);
        assertNotNull(json);

        task.doPost(req, resp);
        int status = resp.getStatus();
        assertEquals(Const.HTTP_STATUS_BAD_REQUEST, status );

    }
}
