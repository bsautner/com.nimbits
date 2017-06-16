package com.nimbits.it.basic;

import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class SnapshotTestAbstract extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void testPostSnapshot() throws InterruptedException {
        final String pointName = UUID.randomUUID().toString();



        nimbits.addPoint(user, new Topic.Builder().name(pointName).create());

        nimbits.setSnapshot(pointName, new Value.Builder().doubleValue(11.0).create());

        sleep();
        Value v = nimbits.getSnapshot(pointName);
        assertEquals(11.0, v.getDoubleValue(), 0.01);
        assertTrue(v.getTimestamp() > 0);



    }
}
