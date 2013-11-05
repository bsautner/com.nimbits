/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.process.cron;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;


public class IdlePointCronTest extends NimbitsServletTest {


    @Test
    public void processGetTest() throws IOException {

        point.setIdleSeconds(1);
        point.setIdleAlarmOn(true);

        entityService.addUpdateEntity(user, Arrays.<Entity>asList(point));
        idleCron.doGet(req, resp);
        final int c = idleCron.processGet();
        assertEquals(1, c);


    }


    @Test
    public void testIdle() throws Exception, InterruptedException {

        point.setIdleAlarmOn(true);
        point.setIdleSeconds(1);
        entityService.addUpdateEntity(user, Arrays.<Entity>asList(point));
        Value vx = ValueFactory.createValueModel(1.2);
        valueService.recordValue(user, point, vx);
        Thread.sleep(2000);
        assertTrue(valueService.checkIdle(user, point));
        Point up = (Point) entityService.getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertTrue(up.getIdleAlarmSent());
        Value vx2 = ValueFactory.createValueModel(21.2);
        valueService.recordValue(user, up, vx2);
        assertFalse(valueService.checkIdle(user, up));
        Thread.sleep(2000);
        Point up2 = (Point) entityService.getEntityByKey(user, point.getKey(), EntityType.point).get(0);

        up2.setIdleAlarmSent(false); //should have been done by the record value task which unit tests don't start

        entityService.addUpdateEntity(user, Arrays.<Entity>asList(up2));
        assertFalse(up2.getIdleAlarmSent());
        assertTrue(valueService.checkIdle(user, up2));
    }


}
