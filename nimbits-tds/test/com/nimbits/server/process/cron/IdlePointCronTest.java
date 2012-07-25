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
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/5/12
 * Time: 7:39 AM
 */
public class IdlePointCronTest extends NimbitsServletTest {


    @Test
    public void processGetTest(){

        point.setIdleSeconds(1);
        point.setIdleAlarmOn(true);
        try {
           EntityServiceFactory.getInstance().addUpdateEntity(user, point);
           final int c =  IdlePointCron.processGet();
           assertEquals(1, c);
        } catch (NimbitsException e) {
            fail();
        }



    }


    @Test
    public void testIdle() throws NimbitsException, InterruptedException {

        point.setIdleAlarmOn(true);
        point.setIdleSeconds(1);
        EntityServiceFactory.getInstance().addUpdateEntity(user, point);
        Value vx = ValueFactory.createValueModel(1.2);
        ValueServiceFactory.getInstance().recordValue(user, point, vx);
        Thread.sleep(2000);
        assertTrue(IdlePointCron.checkIdle(point));
        Point up = (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), EntityType.point).get(0);
        assertTrue(up.getIdleAlarmSent());
        Value vx2 = ValueFactory.createValueModel(21.2);
        ValueServiceFactory.getInstance().recordValue(user, up, vx2);
        assertFalse(IdlePointCron.checkIdle(up));
        Thread.sleep(2000);
        Point up2 = (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), EntityType.point).get(0);

        up2.setIdleAlarmSent(false); //should have been done by the record value task which unit tests don't start

        EntityServiceFactory.getInstance().addUpdateEntity(user, up2);
        assertFalse(up2.getIdleAlarmSent());
        assertTrue(IdlePointCron.checkIdle(up2));
    }


}
