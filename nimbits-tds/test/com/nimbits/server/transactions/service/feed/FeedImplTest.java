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

package com.nimbits.server.transactions.service.feed;

import com.nimbits.client.enums.FeedType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.service.feed.FeedService;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;
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
public class FeedImplTest extends NimbitsServletTest {

  @Resource(name="feedService")
  FeedService i;


    @Test
    public void testPostToFeed() throws Exception {
          i.postToFeed(user, "hello feed", FeedType.info);
          List l = i.getFeed(10,user.getKey() );
          assertTrue(1 == l.size());

    }


    @Test
    public void testEmptyGetFeed() throws Exception {
        List l = i.getFeed(10,user.getKey() );
        assertTrue(l.isEmpty());
    }

    @Test(expected = NimbitsException.class)
    public void testCreateFeedPoint() throws NimbitsException {
        //fails because feeds are created with the user now
        Point p1 =i.createFeedPoint(user);
        assertNotNull(p1);
        assertNotNull(p1.getKey());


    }
    @Test(expected = NimbitsException.class)
    public void testCreateDuplicateFeedCausesErrorPoint() throws NimbitsException {

        Point p1 =i.createFeedPoint(user);
        assertNotNull(p1);
        assertNotNull(p1.getKey());

        Point p2 = i.createFeedPoint(user);
        assertEquals(p2.getKey(), p1.getKey()); //prove a new feed wasn't created.

    }

    @Test
    public void getFeedPoint() throws NimbitsException {
        List<Point> p =  i.getFeedPoint(user);
        assertNotNull(p);
        assertFalse(p.isEmpty());


        List<Point> p3 =  i.getFeedPoint(user);
        assertEquals(p.get(0).getKey(), p3.get(0).getKey()); //prove same feed

    }
}
