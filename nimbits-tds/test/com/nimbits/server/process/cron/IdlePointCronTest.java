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
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/5/12
 * Time: 7:39 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class IdlePointCronTest extends NimbitsServletTest {

    @Resource(name = "entityService")
    EntityService entityService;

    @Resource(name = "valueService")
    ValueService valueService;

    @Resource(name="idleCron")
    IdlePointCron idleCron;

    @Test
    public void processGetTest(){

        point.setIdleSeconds(1);
        point.setIdleAlarmOn(true);
        try {
          entityService.addUpdateEntity(user, point);
           final int c =  idleCron.processGet();
           assertEquals(1, c);
        } catch (NimbitsException e) {
            fail();
        }



    }


    @Test
    public void testIdle() throws NimbitsException, InterruptedException {

        point.setIdleAlarmOn(true);
        point.setIdleSeconds(1);
        entityService.addUpdateEntity(user, point);
        Value vx = ValueFactory.createValueModel(1.2);
        valueService.recordValue(user, point, vx);
        Thread.sleep(2000);
        assertTrue(idleCron.checkIdle(point));
        Point up = (Point) entityService.getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertTrue(up.getIdleAlarmSent());
        Value vx2 = ValueFactory.createValueModel(21.2);
        valueService.recordValue(user, up, vx2);
        assertFalse(idleCron.checkIdle(up));
        Thread.sleep(2000);
        Point up2 = (Point) entityService.getEntityByKey(user, point.getKey(), EntityType.point).get(0);

        up2.setIdleAlarmSent(false); //should have been done by the record value task which unit tests don't start

        entityService.addUpdateEntity(user, up2);
        assertFalse(up2.getIdleAlarmSent());
        assertTrue(idleCron.checkIdle(up2));
    }


}
