package com.nimbits.it.basic;

import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.Event;
import com.nimbits.client.model.Entity;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IdleTopicTestAbstract extends AbstractNimbitsTest {


    @Before
    public void setup() {

        List<Entity> entityList = nimbits.getChildren(nimbits.getMe());
        for (Entity e : entityList) {
            nimbits.deleteEntity(e);
        }


    }
    @After
    public void tearDown() {

    }

    @Test
    public void testGettingIdlePoints() {

        User me = nimbits.getMe(false);
        List<Topic> idleTopics = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            Topic p = nimbits.addPoint(me, new Topic.Builder().create());
            nimbits.addEvent(p, new Event.Builder().eventType(EventType.idle).eventValue(30).create());

            idleTopics.add(p);
            log("Creating idle topic " + i);


        }
        sleep();
        for (Topic e : idleTopics) {
            nimbits.recordValue(e, new Value.Builder().data(UUID.randomUUID().toString()).create());
            log("recording a value to " + e.getName());
        }
        sleep();

        //TODO URGENT- verify idle points
//        for (Topic e : idleTopics) {
//            log("Verifying " + e.getName());
//            Topic r = nimbits.getPoint(e.getId());
//            assertTrue(r.isIdleAlarmOn());
//            assertFalse(r.idleAlarmSent());
//
//
//
//        }



    }

    @Test
    public void testTimeStamp() {

        System.out.println(System.currentTimeMillis());


    }

}
