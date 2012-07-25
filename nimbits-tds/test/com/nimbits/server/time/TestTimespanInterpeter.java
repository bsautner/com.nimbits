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

package com.nimbits.server.time;/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import com.nimbits.client.exception.*;
import com.nimbits.client.model.timespan.*;
import org.junit.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/25/11
 * Time: 7:48 PM
 */
public class TestTimespanInterpeter  {

    @Test
    public void testAbsolute() throws NimbitsException {
        String s1 = "20100410000000";
        String s2 = "20110410000000";
        Timespan ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        Assert.assertNotNull(ts);
        Assert.assertEquals(ts.getStart().getTime(), 1270872000000L);
        Assert.assertEquals(ts.getEnd().getTime(), 1302408000000L);
    }

    @Test
    public void testEpoch() throws NimbitsException {
        String s1 = "1270872000";
        String s2 = "1302408000";
        Timespan ts = null;

        ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);


        Assert.assertEquals(ts.getStart().getTime(), 1270872000000L);
        Assert.assertEquals(ts.getEnd().getTime(), 1302408000000L);
        Assert.assertNotNull(ts);

    }

    @Test
    public void testEpochMs() {
        String s1 = "1270872000000";
        String s2 = "1302408000000";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Assert.assertEquals(ts.getStart().getTime(), 1270872000000L);
        Assert.assertEquals(ts.getEnd().getTime(), 1302408000000L);
        Assert.assertNotNull(ts);

    }

    @Test
    public void testTime5() throws NimbitsException {
        String s1 = "05/09/2011 07:01:44 PM";
        String s2 = "05/09/2011 08:40:44 PM"; //assumes GMT
        Timespan ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        Assert.assertEquals(1304938904000L, ts.getStart().getTime());
        Assert.assertEquals(1304944844000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);
    }


    @Test
    public void testTime() {
        String s1 = "02/23/2010 11:29:08 AM";
        String s2 = "02/23/2011 11:29:08 AM";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Assert.assertEquals(1266942548000L, ts.getStart().getTime());
        Assert.assertEquals(1298478548000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);

    }

    @Test
    public void testTime2() {
        String s1 = "02/23/2010 11:29:08 AM";
        String s2 = "2/23/2011 11:29:08 PM";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Assert.assertEquals(1266942548000L, ts.getStart().getTime());
        Assert.assertEquals(1298478548000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);

    }

    @Test
    public void testTime3() {
        String s1 = "02/23/2010 11:29:08 AM";
        String s2 = "2/23/2011 01:29:08 PM";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(ts.getEnd());

        Assert.assertEquals(1266942548000L, ts.getStart().getTime());
        Assert.assertEquals(1298442548000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);

    }

    @Test
    public void testSQLTime5() {
        String s1 = "04/09/2011 09:10:44 AM";
        String s2 = "04/09/2011 09:10:45 AM";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(ts.getEnd());

        Assert.assertEquals(1302354644000L, ts.getStart().getTime());
        Assert.assertEquals(1302354645000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);

    }

    @Test
    public void testTime43() {
        String s1 = "23/2/2010 11:29:08";
        String s2 = "23/2/2011 01:29:08";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(ts.getEnd());

        Assert.assertEquals(1266942548000L, ts.getStart().getTime());
        Assert.assertEquals(1298442548000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);

    }
    @Test
    public void testTime4333() {
        String s1 = "2/23/2010 11:29:08";
        String s2 = "2/23/2011 01:29:08";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(ts.getEnd());

        Assert.assertEquals(1266942548000L, ts.getStart().getTime());
        Assert.assertEquals(1298442548000L, ts.getEnd().getTime());
        Assert.assertNotNull(ts);

    }
    @Test
    public void testSpecial() {
        String s1 = "-1s";
        String s2 = "*";
        Timespan ts = null;
        try {
            ts = TimespanServiceFactory.getInstance().createTimespan(s1, s2);
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(ts.getStart().getTime());
        System.out.println(ts.getEnd().getTime());
        long r = ts.getEnd().getTime() - ts.getStart().getTime();
        Assert.assertEquals(r, 1000);

//        Assert.assertEquals(ts.getStart().getTime(), 1270872000000L);
//        Assert.assertEquals(ts.getEnd().getTime(), 1302408000000L);
//                Assert.assertNotNull(ts);

    }


}
