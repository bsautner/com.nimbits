package com.nimbits.it.basic;

import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RoundRobinTestAbstract extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void pointUpdateTest() {

        User me = nimbits.getMe(false);
        Topic p1 = nimbits.addPoint(me, new Topic.Builder()

                .create());


        Topic p2 = nimbits.getPoint(p1.getId());
        Topic p3 = nimbits.getPoint(p1.getId());

        assertTrue(p1.equals(p2));
        assertTrue(p1.equals(p3));


        nimbits.updateEntity(p1);


        Topic p4 = nimbits.getPoint(p1.getId());
        Topic p5 = nimbits.getPoint(p1.getId());

        assertTrue(p1.equals(p4));
        assertTrue(p1.equals(p5));



    }

}
