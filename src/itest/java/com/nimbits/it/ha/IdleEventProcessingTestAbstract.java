package com.nimbits.it.ha;


import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.Event;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.Subscription;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 *  Idle event.
 Short description: idle event on a nibmits instance considers only those values that were updated on the same instance.

 Scenario:
 Create topic with enabled idle event with idle seconds = 60; make a subscription with webhook for this idle event.
 Make sure this event is enabled on both instances. One of the way is to configure topic on one of the instances, then restart tomcat.
 Record any value to the topic on "n1".
 Wait 30 seconds.
 Record any value to the topic on "n2".
 Wait 30 seconds.
 [Expected]: Idle event is not triggered, since the topic was updated 30 sec ago.
 [Actual]: Idle event is triggered (webhook is pushed).
 */
public class IdleEventProcessingTestAbstract extends AbstractNimbitsTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testIdleWithWebHooks() throws InterruptedException {

        int idleTime = 10;

        String url = "http://echo.jsontest.com/time/";

        log("Creating an idle enabled topic");
        Topic idle1 = nimbits.addPoint(user, new Topic.Builder()

                .create());

        Event event = nimbits.addEvent(idle1, new Event.Builder().eventType(EventType.idle).eventValue(idleTime).create());

        log("creating a topic to store the result of a web hook");
        Topic webHookTarget1 = nimbits.addPoint(user, new Topic.Builder()
                .create());

        log("creating a webhook that will download from echo service");
        WebHook webHook = nimbits.addWebHook(user, new WebHook.Builder()
                .target(webHookTarget1)
                .method(HttpMethod.GET)
                .url(url)
                .pathChannel(DataChannel.timestamp)
                .enabled(true)

                .create());


        log("creating a subscription to fire the webhook when topic goes idle");
        nimbits.addSubscription(user, new Subscription.Builder()
                .eventType(EventType.idle)
                .subscribedEntity(idle1)
                .target(webHook)
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .enabled(true)
                .create());


        log("recording a value, topic will go idle in " + idleTime + " seconds");
        nimbits.recordValue(idle1, new Value.Builder().data(UUID.randomUUID().toString()).create());


        //give time for idle topic to be processed
        sleep(5);

        log("getting webhook target");
        Value echoDownload1 = nimbits.getSnapshot(webHookTarget1);
        log(echoDownload1);

        assertNotNull(echoDownload1);

       // Echo echo = GsonFactory.getInstance(false).fromJson(echoDownload1.getData(), Echo.class);

      //  assertNotNull(echo.getTime());


    }


    private class Echo {
        Long time;

        public Echo() {
        }

        public Long getTime() {
            return time;
        }
    }
}
