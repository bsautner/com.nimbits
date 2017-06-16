package com.nimbits.it.basic;

import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.model.*;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Create each type of entity and verify it does what it should
 */
public class V3EntityTests extends AbstractNimbitsTest {
    private Group group;
    private Topic outputTopic;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        group = nimbits.addCategory(user, new Group.Builder().create());
        outputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());

    }



    @Test
    public void testValue() throws InterruptedException {
        String foo = "foo";
        Date start = new Date();
        Topic inputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());
        log("input topic id: " + inputTopic.getId());


        nimbits.recordValue(inputTopic, new Value.Builder().meta(foo).create());

        sleep();

        Value value = nimbits.getSnapshot(inputTopic);
        log(value.toString());
        if (!foo.equals(value.getMetaData())) {
            error("value did not record correctly");
        }

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            nimbits.recordValueSync(inputTopic.getName(), new Value.Builder().doubleValue(random.nextDouble() * 100).meta(foo).create());

        }

        int count = 10;
        List<Value> values = nimbits.getValues(inputTopic, start, new Date(), count, foo);
        assertEquals(count, values.size());

    }


    @Test
    public void testSchedule() throws InterruptedException {

        Topic inputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name("name_" + UUID.randomUUID().toString())
                .create());

        Topic output = nimbits.addPoint(group, new Topic.Builder()
                .name("name_" + UUID.randomUUID().toString())
                .create());

        Calculation calculation = nimbits.addCalc(group, new Calculation.Builder()
                .formula("[" + inputTopic.getName() + "]+1")
                .enabled(true)
                .target(output)

                .execute(false)
                .create()
        );

        Schedule schedule = nimbits.addSchedule(group,
                new Schedule.Builder()
                        .interval("*/5 * * * * *")
                        .listener(calculation)
                        .create());

        log(schedule.toString());

        nimbits.recordValue(inputTopic, new Value.Builder().doubleValue(42.0).create());

        sleep(2);


        Value outputValue = nimbits.getSnapshot(output);

        assertEquals(43, outputValue.getDoubleValue(), 0.001);


    }

    @Test
    public  void testInstance() {


        nimbits.addInstance(group,
                new Instance.Builder()
                        .baseUrl("http://cloud.nimbits.com")
                        .password("TEST")
                        .create());

    }

    @Test @Ignore //TODO
    public void testSummary() throws InterruptedException {

        Group group = nimbits.addCategory(user, new Group.Builder().name("testsummary_" + UUID.randomUUID().toString()).create());
        Topic outputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name("output_" + UUID.randomUUID().toString())
                .create());


        Topic inputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name("input_" + UUID.randomUUID().toString())
                .create());
        log("input::", inputTopic);


        nimbits.addSummary(group,
                new Summary.Builder()
                        .summaryType(SummaryType.average)
                        .summaryIntervalMs(2000L)
                        .target(outputTopic)
                        .trigger(inputTopic)
                        .name("summary_" + UUID.randomUUID().toString())
                        .create());

        sleep();

        for (int i = 0; i < 10; i++) {

            log("Summary Test Recording: " + i);
            nimbits.recordValueSync(inputTopic.getName(), new Value.Builder().doubleValue(i).create());
            Thread.sleep(500);

        }
        sleep();
        List<Value> values = nimbits.getValues(inputTopic, 10);
        log("uuid", inputTopic.getId());
        assertEquals(10, values.size());

        sleep();

//        Value v = nimbits.getSnapshot(outputTopic);
//        log("Summary Test:" + v.toString());
//        if (v.getDoubleValue() <= 3) {
//            error("summary didn't work:" + v.toString());
//        } else {
//            log("Done Summary");
//        }

    }

    @Test
    public  void testWebhook() throws InterruptedException {
        Topic inputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());
        List<Entity> children = nimbits.getChildren(group);
        if (!children.contains(inputTopic)) {
            error("Adding a topic to a group didn't work");
        }

        //create a webhook and a subscription to the input topic configured to execute the hook when a new value is recorded.
        WebHook webHook = nimbits.addWebHook(group, new WebHook.Builder()

                .url("http://time.jsontest.com/")
                .method(HttpMethod.GET)
                .target(outputTopic)
                .create());

        nimbits.addSubscription(group, new Subscription.Builder()
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .subscribedEntity(inputTopic)
                .target(webHook)
                .eventType(EventType.newValue)

                .create());

        //record a new value and verify the hook executed and put the result in the output topic

        double testValue = new Random().nextDouble() * 100;
        nimbits.recordValue(inputTopic, new Value.Builder().doubleValue(testValue).create());

        sleep();

        Value value = nimbits.getSnapshot(outputTopic);


        if (value.getData() != null) {

            Long tx = Long.valueOf(value.getData());
            long difference = System.currentTimeMillis() - tx;
            if (difference > 10000) {
                error("Web hook to time api didn't work");
            }
        }

    }

    @Test
    public  void testCalc() throws InterruptedException {


        Topic triggerTopic = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Topic targetTopic = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());



        Calculation calculation = nimbits.addCalc(group, new Calculation.Builder()
                .formula("[" + triggerTopic.getName()+ "]" + "*2")
                .trigger(triggerTopic)
                .target(targetTopic)
                .execute(true)
                .enabled(true)
                .create());

        log("*******Created Calc With ID", calculation.getId());
        log("*******Created Calc With Listener", triggerTopic.getId());
        log("*******Created Calc With Target", targetTopic.getId());



        log("formula", calculation.getFormula());

        nap();
        double testValue3 = new Random().nextDouble() * 100;
        log("sending into calc: " + testValue3);

        nimbits.recordValue(triggerTopic, new Value.Builder().doubleValue(testValue3).create());

        sleep(2);


        Value snapshot3 = nimbits.getSnapshot(targetTopic);

        log("calc: " + calculation.toString());
        log("calc: " + snapshot3.toString());
        log(snapshot3.toString());
        assertEquals(snapshot3.getDoubleValue(), (testValue3 * 2), 0.001);


    }

    @Test @Ignore
    public  void testSync() throws InterruptedException {
        Topic outputTopic2 = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Topic inputTopic = nimbits.addPoint(group, new Topic.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        //sync will pass on any new value to the target - you can use a url of another instance, for this test we loop back on the current instance.
        Sync sync = nimbits.addSync(group, new Sync.Builder()
                .trigger(inputTopic)
                .target(outputTopic2)
                .targetInstance(host)
                .accessKey(password)
                .create());

        log(sync.toString());

        double testValue2 = new Random().nextDouble() * 100;
        nimbits.recordValue(inputTopic, new Value.Builder().doubleValue(testValue2).create());

       sleep();


        Value snapshot = nimbits.getSnapshot(outputTopic2);
        log("sync: " + snapshot.toString());
        if (snapshot.getDoubleValue() != testValue2) {
            error("sync didn't work");
        }
    }
}
