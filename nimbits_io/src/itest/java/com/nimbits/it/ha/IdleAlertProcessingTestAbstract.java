package com.nimbits.it.ha;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.it.AbstractNimbitsTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 *  Idle alert.
 Short description: idle alert on a nibmits instance considers only those values that were updated on the same instance.

 Scenario:
 Create datapoint with enabled idle alert with idle seconds = 60; make a subscription with webhook for this idle alert.
 Make sure this alert is enabled on both instances. One of the way is to configure datapoint on one of the instances, then restart tomcat.
 Record any value to the datapoint on "n1".
 Wait 30 seconds.
 Record any value to the datapoint on "n2".
 Wait 30 seconds.
 [Expected]: Idle alert is not triggered, since the point was updated 30 sec ago.
 [Actual]: Idle alert is triggered (webhook is pushed).
 */
public class IdleAlertProcessingTestAbstract extends AbstractNimbitsTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testIdleWithWebHooks() throws InterruptedException {

        int idleTime = 10;

        String url = "http://echo.jsontest.com/time/";

        log("Creating an idle enabled point");
        Point idle1 = nimbits.addPoint(user, new PointModel.Builder()
                .idleAlarmOn(true)
                .idleSeconds(idleTime)
                .create());

        log("creating a point to store the result of a web hook");
        Point webHookTarget1 = nimbits.addPoint(user, new PointModel.Builder()
                .create());

        log("creating a webhook that will download from echo service");
        WebHook webHook = nimbits.addWebHook(user, new WebHookModel.Builder()
                .downloadTarget(webHookTarget1)
                .method(HttpMethod.GET)
                .url(url)
                .pathChannel(DataChannel.timestamp)
                .enabled(true)

                .create());


        log("creating a subscription to fire the webhook when point goes idle");
        nimbits.addSubscription(user, new SubscriptionModel.Builder()
                .subscriptionType(SubscriptionType.idle)
                .subscribedEntity(idle1)
                .target(webHook)
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .enabled(true)
                .create());


        log("recording a value, point will go idle in " + idleTime + " seconds");
        nimbits.recordValue(idle1, new Value.Builder().data(UUID.randomUUID().toString()).create());


        //give time for idle point to be processed
        sleep(5);

        log("getting webhook target");
        Value echoDownload1 = nimbits.getSnapshot(webHookTarget1);
        log(echoDownload1);

        assertNotNull(echoDownload1);

        Echo echo = GsonFactory.getInstance(false).fromJson(echoDownload1.getData(), Echo.class);

        assertNotNull(echo.getTime());


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
