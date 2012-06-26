package com.nimbits.server.transactions.service.feed;

import com.nimbits.client.enums.FeedType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FeedImplTest extends NimbitsServletTest {

    private FeedImpl i = new FeedImpl();

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

    @Test
    public void testCreateFeedPoint() throws NimbitsException {

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
        Point p =  i.getFeedPoint(user);
        assertNotNull(p);


        Point p3 =  i.getFeedPoint(user);
        assertEquals(p.getKey(), p3.getKey()); //prove same feed

    }
}
