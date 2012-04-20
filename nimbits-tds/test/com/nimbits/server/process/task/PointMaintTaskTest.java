/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.process.task;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.entity.EntityTransactionFactory;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/7/12
 * Time: 9:28 AM
 */
public class PointMaintTaskTest extends NimbitsServletTest {


    @Test
    public void testGet() throws NimbitsException {

        final Map<String,Entity> e = EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(EntityType.point);
        assertTrue(e.size() > 0);

        for (final Entity en : e.values()) {
          final String j = GsonFactory.getInstance().toJson(en);
          req.setParameter(Parameters.json.getText(), j);
          assertNotNull(req.getParameter(Parameters.json.getText()));
            PointMaintTask.processPost(req, resp);

        }

    }
}
