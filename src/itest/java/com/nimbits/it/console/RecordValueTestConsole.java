package com.nimbits.it.console;

import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

@Ignore
public class RecordValueTestConsole extends AbstractNimbitsTest {

    @Test
    public void runTest() throws InterruptedException {
        Topic topic = nimbits.addPoint(user, new Topic.Builder().create());


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
