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

package com.nimbits.server.orm;

import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;


public class PointEntityTest extends NimbitsServletTest {

    @Test
    public void testValidation() throws Exception {
        PointEntity e = new PointEntity(point);
        e.setIdleAlarmOn(true);
        e.validate(user);

    }

    @Test(expected = Exception.class)
    public void testValidation2() throws Exception {
        PointEntity e = new PointEntity(point);
        e.setIdleAlarmOn(true);
        e.setHighAlarmOn(true);
        e.setLowAlarmOn(true);
        e.setHighAlarm(0.0);
        e.setLowAlarm(100);
        e.validate(user);

    }

    @Test
    public void testValidation3() throws Exception {
        PointEntity e = new PointEntity(point);
        e.setIdleAlarmOn(true);
        e.setHighAlarmOn(false);
        e.setLowAlarmOn(true);
        e.setHighAlarm(0.0);
        e.setLowAlarm(100);
        e.validate(user);

    }

}
