package com.nimbits.it.basic;

import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.model.Group;
import com.nimbits.client.model.Entity;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class V3FindThingsExamples extends AbstractNimbitsTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeTest() throws InterruptedException {


        String pointName = UUID.randomUUID().toString();

        //create a topic under the top level user with a random name
        Topic topic = new Topic.Builder().name(pointName).parent(user.getId())

                .create();
        Entity newPoint = nimbits.addPoint(user, topic);
        log("Created : " + newPoint.getName());

        Optional<Topic> foundPoint = nimbits.findTopicByName(pointName);

        //TODO URGENT - add alarms and verify on and off

        if (foundPoint.isPresent()) {
//            log("verified topic");
//            if (!foundPoint.get().isHighAlarmOn() || !foundPoint.get().isLowAlarmOn()) {
//                throw new RuntimeException(" Alarm was off when it was set to on!");
//
//            }
        } else {
            throw new RuntimeException("Topic not found after being created and searched for");
        }

        //search for a topic that was never created to test absent condition
        try {
            Optional<Topic> shouldNotExist = nimbits.findTopicByName(UUID.randomUUID().toString());

        } catch (NimbitsClientException error) {
            fail(); //todo
            //assertEquals(404, error.getResponse().getStatus());
        }


        //Record some values with the name only
        double testValue = 100.00 * new Random().nextDouble();
        Value value = new Value.Builder().doubleValue(testValue).create();
        nimbits.recordValue(pointName, value);
        Thread.sleep(1000);
        Value retrieved = nimbits.getSnapshot(pointName);
        assertEquals(value.getDoubleValue(), retrieved.getDoubleValue(), 0.001);


        veryifyFindCategory();
        veryifyFindWebHook();

    }

    private void veryifyFindCategory() {
        String name = UUID.randomUUID().toString();

        try {
            Optional<Group> result = nimbits.findCategory("i dont exist");

        } catch (NimbitsClientException error) {
            fail(); //todo
           // assertEquals(404, error..getStatus());
        }


        Group group = new Group.Builder().name(name).create();
        nimbits.addCategory(user, group);

        Optional<Group> verify = nimbits.findCategory(name);
        assertTrue(verify.isPresent());
    }

    private void veryifyFindWebHook() {
        String name = UUID.randomUUID().toString();


        try {
         Optional<WebHook> result = nimbits.findWebHook("i dont exist");

        } catch (NimbitsClientException error) {
            fail(); //todo
           //
            // assertEquals(404, error.getResponse().getStatus());
        }

        WebHook webHook = new WebHook.Builder()
                .name(name)
                .method(HttpMethod.GET)
                .url("http://www.foo.com")
                .create();
        nimbits.addWebHook(user, webHook);

        Optional<WebHook> verify = nimbits.findWebHook(name);
        assertTrue(verify.isPresent());
    }


}



