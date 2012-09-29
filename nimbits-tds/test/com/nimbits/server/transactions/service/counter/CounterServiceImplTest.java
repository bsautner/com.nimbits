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

package com.nimbits.server.transactions.service.counter;

import com.nimbits.server.NimbitsServletTest;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 2:42 PM
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
public class CounterServiceImplTest extends NimbitsServletTest{

    @Resource(name="counterService")
    CounterService counterService;

    @Test
    public void testIncrementCounter() throws Exception {
        counterService.createShards("foo");

        for (int i = 0; i < 10; i++) {
            counterService.incrementCounter("foo");
        }
        Assert.assertEquals(counterService.getCount("foo"), 10);

    }

    @Test
    public void testIncrementDateCounter() throws Exception {
        counterService.updateDateCounter("foo");
        Date t = null;
        for (int i = 0; i < 10; i++) {

             t = counterService.updateDateCounter("foo");

        }
        assertNotNull(t);
        Assert.assertEquals(counterService.getDateCounter("foo").getTime(), t.getTime());


    }


    @Test
    public void testGetCount() throws Exception {
        counterService.createShards("foo");

        Assert.assertEquals(counterService.getCount("foo"), 0);
    }
}
