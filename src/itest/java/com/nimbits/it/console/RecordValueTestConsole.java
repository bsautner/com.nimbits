package com.nimbits.it.console;

import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.Event;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;


public class RecordValueTestConsole extends AbstractNimbitsTest {

    @Test
    public void runTest() throws InterruptedException {
        Topic topic = nimbits.addPoint(user, new Topic.Builder().create());

        Event event = nimbits.addEvent(topic, new Event.Builder()
                .name("high1")
                .enabled(true)
                .execute(true)
                .eventType(EventType.high)
                .eventValue(60)
                .create());

        Event eventLow = nimbits.addEvent(topic, new Event.Builder()
                .name("low1")
                .enabled(true)
                .execute(true)
                .eventType(EventType.high)
                .eventValue(40)
                .create());


        Random random = new Random();
        int i = 0;
        while (true) {
            System.out.println("recording random value " + i++);
            try {
                nimbits.recordValue(topic, new Value.Builder().doubleValue(random.nextDouble() * 100).create());
            } catch (Throwable throwable) {
                System.out.println("failed with: " + throwable.getMessage());
            }


            Thread.sleep(5000);
        }


    }
}
