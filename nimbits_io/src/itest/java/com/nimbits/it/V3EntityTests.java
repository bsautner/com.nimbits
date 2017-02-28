package com.nimbits.it;

import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.InstanceModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.schedule.ScheduleModel;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.summary.SummaryModel;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.sync.SyncModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Create each type of entity and verify it does what it should
 */
public class V3EntityTests extends NimbitsTest {
    private Category category;
    private Point outputPoint;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        category = nimbits.addCategory(user, new CategoryModel.Builder().create());
        outputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

    }



    @Test
    public void testValue() throws InterruptedException {
        String foo = "foo";
        Date start = new Date();
        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .highAlarmOn(true)
                .create());
        log("input point id: " + inputPoint.getId());

        nimbits.recordValue(inputPoint, new Value.Builder().meta(foo).create());

        sleep();

        Value value = nimbits.getSnapshot(inputPoint);
        log(value.toString());
        if (!foo.equals(value.getMetaData())) {
            error("value did not record correctly");
        }

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            nimbits.recordValueSync(inputPoint.getName().getValue(), new Value.Builder().doubleValue(random.nextDouble() * 100).meta(foo).create());

        }

        int count = 10;
        List<Value> values = nimbits.getValues(inputPoint, start, new Date(), count, foo);
        assertEquals(count, values.size());

    }


    @Test
    public void testSchedule() throws InterruptedException {

        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Point output = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Schedule schedule = nimbits.addSchedule(category,
                new ScheduleModel.Builder()
                        .interval(500L)
                        .source(inputPoint)
                        .target(output)
                        .create());

        log(schedule.toString());

        nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(42.0).create());

        sleep();


        Value outputValue = nimbits.getSnapshot(output);

        assertNotEquals(42, outputValue.getDoubleValue());


    }

    @Test
    public  void testInstance() {


        nimbits.addInstance(category,
                new InstanceModel.Builder()
                        .baseUrl(UrlContainer.getInstance("cloud.nimbits.com"))
                        .adminEmail(CommonFactory.createEmailAddress("test@example.com"))
                        .version("1.0")
                        .password("TEST")
                        .isDefault(true)
                        .socketsEnabled(true)
                        .serverId(100)
                        .create());

    }

    @Test
    public void testSummary() throws InterruptedException {

        Category category = nimbits.addCategory(user, new CategoryModel.Builder().name("testsummary_" + UUID.randomUUID().toString()).create());
        Point outputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name("output_" + UUID.randomUUID().toString())
                .create());


        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name("input_" + UUID.randomUUID().toString())
                .create());
        log("input::", inputPoint);


        nimbits.addSummary(category,
                new SummaryModel.Builder()
                        .summaryType(SummaryType.average)
                        .summaryIntervalMs(2000L)
                        .target(outputPoint)
                        .trigger(inputPoint)
                        .name("summary_" + UUID.randomUUID().toString())
                        .create());

        sleep();

        for (int i = 0; i < 10; i++) {

            log("Summary Test Recording: " + i);
            nimbits.recordValueSync(inputPoint.getName().getValue(), new Value.Builder().doubleValue(i).create());
            Thread.sleep(500);

        }
        sleep();
        List<Value> values = nimbits.getValues(inputPoint, 10);
        log("uuid", inputPoint.getId());
        assertEquals(10, values.size());

        sleep();

//        Value v = nimbits.getSnapshot(outputPoint);
//        log("Summary Test:" + v.toString());
//        if (v.getDoubleValue() <= 3) {
//            error("summary didn't work:" + v.toString());
//        } else {
//            log("Done Summary");
//        }

    }

    @Test
    public  void testWebhook() throws InterruptedException {
        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());
        List<Entity> children = nimbits.getChildren(category);
        if (!children.contains(inputPoint)) {
            error("Adding a point to a folder didn't work");
        }

        //create a webhook and a subscription to the input point configured to execute the hook when a new value is recorded.
        WebHook webHook = nimbits.addWebHook(category, new WebHookModel.Builder()

                .url("http://time.jsontest.com/")
                .method(HttpMethod.GET)
                .downloadTarget(outputPoint)
                .create());

        nimbits.addSubscription(category, new SubscriptionModel.Builder()
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .subscribedEntity(inputPoint)
                .target(webHook)
                .subscriptionType(SubscriptionType.newValue)

                .create());

        //record a new value and verify the hook executed and put the result in the output point

        double testValue = new Random().nextDouble() * 100;
        nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(testValue).create());

        sleep();

        Value value = nimbits.getSnapshot(outputPoint);


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
        Point outputPointCalc = nimbits.addPoint(category, new PointModel.Builder()
                .name("calc output " + UUID.randomUUID().toString())
                .create());

        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());


        Calculation calculation = nimbits.addCalc(category, new CalculationModel.Builder()
                .formula("x*2")
                .trigger(inputPoint)
                .x(inputPoint)
                .target(outputPointCalc)
                .create());

        double testValue3 = new Random().nextDouble() * 100;
        log("sending into calc: " + testValue3);
        nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(testValue3).create());

        sleep();


        Value snapshot3 = nimbits.getSnapshot(outputPointCalc);

        log(snapshot3.toString());
        if (snapshot3.getDoubleValue() != testValue3 * 2) {
            error("calc failed: " + testValue3 + "vs" + snapshot3.getDoubleValue());
        }
        log("calc: " + calculation.toString());
        log("calc: " + snapshot3.toString());
    }

    @Test @Ignore
    public  void testSync() throws InterruptedException {
        Point outputPoint2 = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        //sync will pass on any new value to the target - you can use a url of another instance, for this test we loop back on the current instance.
        Sync sync = nimbits.addSync(category, new SyncModel.Builder()
                .trigger(inputPoint)
                .target(outputPoint2)
                .targetInstance(INSTANCE_URL)
                .accessKey(PASSWORD)
                .create());

        log(sync.toString());

        double testValue2 = new Random().nextDouble() * 100;
        nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(testValue2).create());

       sleep();


        Value snapshot = nimbits.getSnapshot(outputPoint2);
        log("sync: " + snapshot.toString());
        if (snapshot.getDoubleValue() != testValue2) {
            error("sync didn't work");
        }
    }
}
