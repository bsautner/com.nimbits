/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
