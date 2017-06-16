package com.nimbits.it.console;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.Event;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.Subscription;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scenario - create several idle points that email me if they go idle, but write to them so they go back to
 * being not idle, then let them fall back to being idle so i get repeated email events
 */
@Ignore
public class IdleEventSpamMe extends AbstractNimbitsTest {



    public static void main(String... args) throws Exception {

        IdleEventSpamMe test = new IdleEventSpamMe();
        test.setUp();
        test.runTest();


    }

    public void runTest() {
        List<Topic> topicList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Topic topic = nimbits.addPoint(user, new Topic.Builder().create());

            nimbits.addEvent(topic, new Event.Builder().eventType(EventType.idle).eventValue(60).create());

            System.out.println("Added Topic " + topic.getName());
            topicList.add(topic);
            nimbits.addSubscription(topic, new Subscription.Builder()
                    .eventType(EventType.idle)
                    .subscribedEntity(topic)
                    .enabled(true)
                    .notifyMethod(SubscriptionNotifyMethod.email)
                    .target("bsautner@gmail.com")
                    .create());
        }

        Random r = new Random();
        for (Topic p : topicList) {
            nimbits.recordValueSync(p.getName(), new Value.Builder().doubleValue(r.nextDouble() * 100).create());
        }



        for (Topic p : topicList) {
            System.out.println(nimbits.getSnapshot(p));
        }

    }
}
