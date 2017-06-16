package com.nimbits.it.heartbeat;


import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.topic.TopicType;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Scenario - a topic with type "hearbeat" should only produce one valuestore record with the current value
 */
public class HeartbeatTest extends AbstractBaseNimbitsTest {
    private final int count = 10;

    private double lastValueSent = 0.0;
    private Long lastTimestampSent;
    private Value lastValueRecorded;


    @Test
    public void testHeartBeat() throws InterruptedException {

        String name = UUID.randomUUID().toString();
        Topic topic = adminClient.addPoint(adminUser,
                new Topic.Builder().name(name).pointType(TopicType.heartbeat)
                        .create());

        assertNotNull(topic.getId());
        recordAlternating(topic);
        List<Value> getSeries = adminClient.getValues(topic, count);
        assertEquals(1, getSeries.size());
        assertEquals(lastTimestampSent, getSeries.get(0).getTimestamp());

    }




    private void recordAlternating(Topic topic) throws InterruptedException {

        for (int i = 0; i < count; i++) {

            if (lastValueSent == 0.0) {
                lastValueSent = 1.0;
            }
            else {
                lastValueSent = 0.0;
            }
            lastTimestampSent = System.currentTimeMillis();
            lastValueRecorded = adminClient.recordValueSync(topic.getName(),
                    new Value.Builder()
                            .timestamp(lastTimestampSent)
                            .doubleValue(lastValueSent)
                            .create());
            log(lastValueRecorded);
            Thread.sleep(10);
        }



    }
}
