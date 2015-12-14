import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.sync.SyncModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Create each type of entity and verify it does what it should
 */
public class V3EntityTests extends NimbitsTest {


    public static void main(String... args) throws InterruptedException {

        V3EntityTests test = new V3EntityTests();
        test.execute();



    }

    @Override
    public void execute() throws InterruptedException {
        super.execute();


        //create a folder for putting things in, my user account object is the folders parent in the tree (user is setup in the super.execute).
        Category category = nimbits.addCategory(user, new CategoryModel.Builder().create());


        //Create a data point for recording values
        Point inputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Point inputPoint2 = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Point outputPoint = nimbits.addPoint(category, new PointModel.Builder()
                .name(UUID.randomUUID().toString())
                .create());

        Point outputPoint2 = nimbits.addPoint(category, new PointModel.Builder()
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

        log(user.toString());
        log(inputPoint.toString());
        log(subscription.toString());
        log(webHook.toString());

        Point outputPointCalc = nimbits.addPoint(category, new PointModel.Builder()
                .name("calc output " + UUID.randomUUID().toString())
                .create());

        Calculation calculation = nimbits.addCalc(category, new CalculationModel.Builder()
                .formula("x*2")
                .trigger(inputPoint2)
                .x(inputPoint2)
                .target(outputPointCalc)
                .create());

        double testValue3 = new Random().nextDouble() * 100;
        log("sending into calc: " + testValue3);
        nimbits.recordValue(inputPoint2, new Value.Builder().doubleValue(testValue3).create());

        Thread.sleep(2000);


        Value snapshot3 = nimbits.getSnapshot(outputPointCalc);

        log(snapshot3.toString());
        if (snapshot3.getDoubleValue() != testValue3 * 2) {
            error("calc failed: " + testValue3  + "vs" + snapshot3.getDoubleValue());
        }
        log("calc: " + calculation.toString());
        log("calc: " + snapshot3.toString());


     



//                summary
//                accessKey
//                instance
//                socket
//                connection
//                schedule
//                webhook

        log("Done " + getClass().getName());

    }
}
