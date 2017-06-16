package com.nimbits.it.basic;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.Subscription;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class V3CreateAndTestWebHooksAbstract extends AbstractNimbitsTest {

    private final static String TAG = V3CreateAndTestWebHooksAbstract.class.getName();

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeTest() throws InterruptedException {


        String targetPointName = TAG + " Download Target Topic " + UUID.randomUUID().toString();
        String triggerPointName = TAG + " Listener Topic " + UUID.randomUUID().toString();
        String subscriptionName = TAG + " Subscription " + UUID.randomUUID().toString();


        Topic downloadTarget = nimbits.addPoint(user,
                new Topic.Builder()
                        .name(targetPointName)
                        .create());

        Topic trigger = nimbits.addPoint(downloadTarget,
                new Topic.Builder()
                        .name(triggerPointName)
                        .create());

        WebHook webHook = nimbits.addWebHook(downloadTarget, new WebHook.Builder()
                .name(TAG + " web hook " + UUID.randomUUID().toString())
                .url("http://scooterlabs.com/echo.json?")
                .method(HttpMethod.GET)
                .target(downloadTarget)
                .pathChannel(DataChannel.data)
                .bodyChannel(DataChannel.meta)
                .create());


        Subscription subscription = nimbits.addSubscription(downloadTarget,
                new Subscription.Builder()
                        .name(subscriptionName)
                        .subscribedEntity(trigger.getId())
                        .notifyMethod(SubscriptionNotifyMethod.webhook)
                        .eventType(EventType.newValue)
                        .target(webHook.getId())
                        .create());

        String data = UUID.randomUUID().toString();

        nimbits.recordValue(trigger, new Value.Builder()
                .data("value=" + data)
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
