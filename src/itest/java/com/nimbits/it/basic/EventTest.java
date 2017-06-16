package com.nimbits.it.basic;

import com.nimbits.client.model.topic.Topic;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventTest extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test @Ignore
    public void executeIdleSetTest() throws InterruptedException {
        doTest(true, true, true);
        doTest(true, true, false);
        doTest(true, false, true);
        doTest(false, true, true);


        doTest(true, false, false);
        doTest(false, true, false);
        doTest(false, false, true);

        doTest(false, false, false);

    }

    private void doTest(boolean idleSetting, boolean lowSetting, boolean highSetting) throws InterruptedException {

        String pointName = UUID.randomUUID().toString();

        final boolean[] gotCallback = {false};

        Topic topic;


        nimbits.addPoint(user, new Topic.Builder().name(pointName).create());

        Optional<Topic> serversVersion;

        serversVersion = nimbits.findTopicByName(pointName);
        assertTrue(serversVersion.isPresent());
        assertNotNull(serversVersion.get().getId());
        topic = serversVersion.get();
        assertEquals(pointName, topic.getName());


        nimbits.updateEntity(topic);

        while (! gotCallback[0]) {
            Thread.sleep(1); //wait for callback
        }
        serversVersion = nimbits.findTopicByName(pointName);
        assertTrue(serversVersion.isPresent());
        topic = serversVersion.get();


    }


}
