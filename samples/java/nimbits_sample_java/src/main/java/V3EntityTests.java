import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyModel;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.connection.ConnectionModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.InstanceModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.schedule.ScheduleModel;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.summary.SummaryModel;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.sync.SyncModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.client.io.Nimbits;
import org.apache.http.util.TextUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Create each type of entity and verify it does what it should
 */
public class V3EntityTests extends NimbitsTest {
    Category category;
    Point outputPoint;

    public static void main(String... args) throws InterruptedException {

        V3EntityTests test = new V3EntityTests();
        test.execute();



    }

    @Override
    public void execute() throws InterruptedException {
        super.execute();


        //create a folder for putting things in, my user account object is the folders parent in the tree (user is setup in the super.execute).
        category = nimbits.addCategory(user, new CategoryModel.Builder().create());
        outputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        testValue();

        testSocket();

        testConnection();

        testSchedule();

        testInstance();

        testAccessKey();

        testWebhook();

        testSync();

        testCalc();

        testSummary();

        log("Done " + getClass().getName());

    }

    private void testValue() throws InterruptedException {
        String foo = "foo";
        Date start = new Date();
        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .highAlarmOn(true)
                .create());

        nimbits.recordValue(inputPoint, new Value.Builder().meta(foo).create());
        Thread.sleep(100);
        Value value = nimbits.getSnapshot(inputPoint);
        log(value.toString());
        if (! foo.equals(value.getMetaData())) {
            error("value did not record correctly");
        }

        Random random  = new Random();
        for (int i = 0; i < 100; i++) {
            nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(random.nextDouble() * 100).meta(foo).create());

        }

        int count = 10;
        List<Value> values = nimbits.getValues(inputPoint, start, new Date(), 10, foo);
        if (values.size() != count) {
            error("Didn't get the count i expected");
        }


    }

    private void testSocket() {

    }

    private void testConnection() {
        Connection connection = nimbits.addConnection(category, new ConnectionModel.Builder()
                .targetEmail("Z" + EMAIL_ADDRESS)
                .create());

        log(connection.toString());
    }

    private void testSchedule() throws InterruptedException {

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

        nimbits.recordValue(inputPoint, new  Value.Builder().doubleValue(42.0).create());

        Thread.sleep(10000);

        Value outputValue = nimbits.getSnapshot(output);

        if (outputValue.getDoubleValue() != 42.0) {
            error("Schedule did not fire at expected interval");
        }



    }

    private void testInstance() {



        nimbits.addInstance(category,
                new InstanceModel.Builder()
                        .baseUrl(UrlContainer.getInstance("cloud.nimbits.com"))
                        .adminEmail(CommonFactory.createEmailAddress("test@example.com"))
                        .version("1.0")
                        .apiKey(new AccessKeyModel.Builder().code("TEST").create())
                        .isDefault(true)
                        .socketsEnabled(true)
                        .serverId(100)
                        .create());

    }

    private void testAccessKey() {

        AccessKey key = new AccessKeyModel.Builder()
                .code("TEST")

                .create();

        nimbits.addAccessKey(category, key);

        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());



        Nimbits clientUsingKey = new Nimbits.Builder().email(EMAIL_ADDRESS).instance(INSTANCE_URL).token(key.getCode()).create();

    Optional<Point> p = clientUsingKey.findPointByName(inputPoint.getName().getValue());
        if (p.isPresent()) {
            log(p.toString());
        }
        else {
            error("getting point with a new access key failed.");
        }

        List<Entity> downloaded = nimbits.getChildren(category);
        for (Entity e : downloaded) {

            if (e.getEntityType().equals(EntityType.accessKey)) {
                AccessKey k = (AccessKey) e;
                if (!TextUtils.isEmpty(k.getCode())) {
                    error("api is returning key codes when it should not");
                }
            }
        }



    }

    private void testSummary() throws InterruptedException {
        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());


       nimbits.addSummary(category,
                new SummaryModel.Builder()
                        .summaryType(SummaryType.average)
                        .summaryIntervalMs(2000L)
                        .target(outputPoint)
                        .trigger(inputPoint)
                        .create());
        Random r = new Random();
        double total = 0;
        for (int i = 0; i < 10; i++) {
            double v = i;//r.nextDouble() * 100;
            total += v;
            log("Summary Test Recording: " + v);
            nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(v).create());
            Thread.sleep(500);

        }

        Value v = nimbits.getSnapshot(outputPoint);
        log("Summary Test:" + v.toString());
        if (v.getDoubleValue() <= 5) {
            error("summary didn't work");
        }
        else {
            log("Done Summary");
        }

    }

    private void testWebhook() throws InterruptedException {
        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());
        List<Entity> children = nimbits.getChildren(category);
        if (! children.contains(inputPoint)) {
            error("Adding a point to a folder didn't work");
        }

        //create a webhook and a subscription to the input point configured to execute the hook when a new value is recorded.
        WebHook webHook = nimbits.addWebHook(category, new WebHookModel.Builder()

                .url("http://cloud.nimbits.com/service/v2/time")
                .method(HttpMethod.GET)
                .downloadTarget(outputPoint)
                .create());

        Subscription subscription = nimbits.addSubscription(category, new SubscriptionModel.Builder()
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .subscribedEntity(inputPoint)
                .target(webHook)
                .subscriptionType(SubscriptionType.newValue)

                .create());

        //record a new value and verify the hook executed and put the result in the output point

        double testValue = new Random().nextDouble() * 100;
        nimbits.recordValue(inputPoint, new Value.Builder().doubleValue(testValue).create());
        Thread.sleep(1000);
        Value value = nimbits.getSnapshot(outputPoint);


        if (value.getData() != null) {

            Long tx = Long.valueOf(value.getData());
            long difference = System.currentTimeMillis() - tx;
            if (difference > 10000) {
                error("Web hook to time api didn't work");
            }
        }

    }

    private void testCalc() throws InterruptedException {
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

        Thread.sleep(2000);


        Value snapshot3 = nimbits.getSnapshot(outputPointCalc);

        log(snapshot3.toString());
        if (snapshot3.getDoubleValue() != testValue3 * 2) {
            error("calc failed: " + testValue3  + "vs" + snapshot3.getDoubleValue());
        }
        log("calc: " + calculation.toString());
        log("calc: " + snapshot3.toString());
    }

    private void testSync() throws InterruptedException {
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

        Thread.sleep(2000);

        Value snapshot = nimbits.getSnapshot(outputPoint2);
        log("sync: " + snapshot.toString());
        if (snapshot.getDoubleValue() != testValue2) {
            error("sync didn't work");
        }
    }
}
