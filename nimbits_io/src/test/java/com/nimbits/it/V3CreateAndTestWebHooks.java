package com.nimbits.it;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.client.io.http.NimbitsClientException;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class V3CreateAndTestWebHooks extends NimbitsTest {

    private final static String TAG = V3CreateAndTestWebHooks.class.getName();

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeTest() throws InterruptedException {


        String targetPointName = TAG + " Download Target Point " + UUID.randomUUID().toString();
        String triggerPointName = TAG + " Trigger Point " + UUID.randomUUID().toString();
        String subscriptionName =  TAG + " Subscription " + UUID.randomUUID().toString();


        Point downloadTarget = nimbits.addPoint(user,
                new PointModel.Builder()
                        .name(targetPointName)
                        .create());

        Point trigger = nimbits.addPoint(downloadTarget,
                new PointModel.Builder()
                        .name(triggerPointName)
                        .create());

        WebHook webHook = nimbits.addWebHook(downloadTarget, new WebHookModel.Builder()
                .name(TAG + " web hook " + UUID.randomUUID().toString())
                .url("http://scooterlabs.com/echo.json?")
                .method(HttpMethod.GET)
                .downloadTarget(downloadTarget.getKey())
                .pathChannel(DataChannel.data)
                .bodyChannel(DataChannel.meta)
                .create());


        Subscription subscription = nimbits.addSubscription(downloadTarget,
                new SubscriptionModel.Builder()
                        .name(subscriptionName)
                        .subscribedEntity(trigger.getKey())
                        .notifyMethod(SubscriptionNotifyMethod.webhook)
                        .subscriptionType(SubscriptionType.newValue)
                        .target(webHook.getKey())
                        .create());

        String data = UUID.randomUUID().toString();

        nimbits.recordValue(trigger, new Value.Builder()
                .data("value=" + data )
        .create());

        Thread.sleep(1000);

        Value snapshot = nimbits.getSnapshot(targetPointName);
        log(snapshot.toString());





        log("cleanup " + TAG);
        try {
           // nimbits.deleteEntity(webHook);
          //  nimbits.deleteEntity(downloadTarget);
        } catch (NimbitsClientException ex) {
            log(ex.getMessage());
            ex.printStackTrace();
        }
        log("done " + TAG);

    }




}
