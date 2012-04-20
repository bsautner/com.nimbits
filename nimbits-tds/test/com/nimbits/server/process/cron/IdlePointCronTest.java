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

package com.nimbits.server.process.cron;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
           // PointServiceFactory.getInstance().updatePoint(user, point);

           final int c =  IdlePointCron.processGet();
            assertEquals(1, c);
        } catch (NimbitsException e) {
            fail();
        }



    }


}
