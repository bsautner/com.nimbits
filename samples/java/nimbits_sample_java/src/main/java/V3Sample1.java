import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.io.Nimbits;

import java.util.Collections;
import java.util.UUID;


public class V3Sample1 {

    /**
     * This sample uses nimbits.io to:
     *
     * 1. Create an admin user on a new nimbits server
     * 2. Create some regular users using the admin's credentials
     * 3. Re-Connect to the server as a regular user
     * 4. Create A folder under the user's account
     * 5. Create Some Data Points in that folder
     * 6. Create a WebHook using code
     * 7. Create Subscriptions for those points that uses that webhook
     * 8. Write some data and verify the subscriptions
     *
     *
     *
     * Note:

     the api is designed to take a POJO and return a new object that is the fully created object with database keys etc
     so code like this is common:

     Foo foo1 = FooBuilder().create();
     Foo foo2 = nimbits.createFoo(foo1);

     foo2 is a new object, returned by the api where foo1 was a DTO used to create foo2.

     to avoid confusion the sample will do:

     Foo foo1 = FooBuilder().create();
     foo1 = nimbits.createFoo(foo1);

     */


    private static final String server = "http://localhost:8888";
    private static final String adminEmail = "admin@example.com";
    private static final String adminPassword = "password12345";



    public static void main(String[] args) throws InterruptedException {


        //1: Create a client using the admin credentials.

        Nimbits adminClient = new Nimbits.NimbitsBuilder()
                .email(adminEmail).token(adminPassword).instance(server).create();


        /*
        1.a : Note that we're pointing to a new server, so we're posting a new admin with the same credentials
         We're surrounding things with try catch blocks so we can run this code without having to clear out the server every time
        */
        try {
            User admin = new UserModel(adminEmail, adminPassword);
            admin = adminClient.addUser(admin);

            Log("Created Admin: " + admin.toString());
        } catch (Throwable throwable) {
            Log(throwable.getMessage());
        }


        //2: Create a new user using the admin client, only admins can create users:
        String email = "user1@example.com";
        String password = "userpassword1234";
        try {

            User basicUser = new UserModel(email, password);
            basicUser = adminClient.addUser(basicUser);

            Log("Created User: " + basicUser.toString());
        } catch (Throwable throwable) {
            Log(throwable.getMessage());
        }


        //3: Create a new client with the user's credentials instead:

        Nimbits client = new Nimbits.NimbitsBuilder()
                .email(email).token(password).instance(server).create();

        User me = client.getMe();

        Log("Re-Downloaded basic user to verify: " + me.toString());


        //4: Create a folder (aka category) with the user entity (me) as the parent (this won't throw an error if a duplicate folder is added, since you can have folders with the same name)

        Category folder = new CategoryModel.CategoryBuilder().name("my folder 6").create();
        folder = client.addCategory(me, folder);
        Log("created folder: " + folder.toString());


        /* 5:
         Create some datapoints - one will be a subscription trigger, so when it's writen to it'll fire off a web hook we're going to create below
         the other will be a target, which is an optional setting for a webhook where the result of the http call will be stored in the dx channel as a new value
         we add the current time to the point name so it's always unique

        */
        Point newTrigger = new PointModel.Builder()
                .name("Data Point Trigger " + System.currentTimeMillis())
                .create();
        Point newTarget = new PointModel.Builder().name("Data Point Target" + System.currentTimeMillis()).create();



        newTrigger = client.addPoint(folder, newTrigger);
        newTarget = client.addPoint(folder, newTarget);
        Log("Created Data Point: " + newTrigger.getUUID() + " " + newTrigger.toString());
        Log("Created Data Point: " + newTarget.getUUID() + " " + newTarget.toString());


        Log("Verified Point using newly created uuid " + client.getPoint(newTarget.getUUID()));
        Log("Verified Point using newly created uuid " + client.getPoint(newTarget.getUUID()));




        //6 Let's create a webhook!

        String timeApi = "http://cloud.nimbits.com/service/v2/time";
        WebHook webHook = new WebHookModel.Builder()
                .name("Web Hook To Time API")
                .setMethod(HttpMethod.GET)
                .setDownloadTarget(newTarget.getKey())
                .setUrl(timeApi)
                .create();

        webHook = client.addWebHook(folder, webHook);

        Log("Created webhook: " + webHook.toString());





        //7: create a subscription so when the trigger point gets new data, webhook will be used

        Subscription subscription = new SubscriptionModel.Builder()
                .subscriptionType(SubscriptionType.newValue)
                .maxRepeat(-1)   //max repeat protects out of control loops etc - the minimum number of seconds to wait before this can run again - setting it to -1 means every subscription will run
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .name("Event Subscription Time API Web Hook Call when Trigger point is written To")
                .target(webHook.getKey()) //note that the subsciption target is the webhook - the webhook target is the target point
                .subscribedEntity(newTrigger.getKey())
                .create();

        subscription = client.addSubscription(folder, subscription);
        Log("Created Subscription: " + subscription.toString());


        //8: Write some data to the trigger point, which will cause the subscription to run - using the webhook to download the current time from the time api

        //8.a Write the data - if the webhook was a post, the data in this value could be the post body and contain JSON. In a GET the data will be the query string
        //in our case, the time api doesn't require any data, but we want to write something so the data isn't filtered out as noise.

        Value value = new Value.ValueBuilder().data("?foo=" + UUID.randomUUID().toString()).createValue();

        client.recordValues(newTrigger, Collections.singletonList(value));

        //That value should now be the current value for the point, let's download the snapshot (aka most recent value) and verify
        Thread.sleep(1000); //give the value time to be processed
        Value snapshot = client.getSnapshot(newTrigger);

        Log("Verified value: " + snapshot.toString());


        //If all went well, the new value posted to the trigger should have caused the webhook to query the time api and save the result in the target point

        Value targetSnapshot = client.getSnapshot(newTarget);

        Log("Verified Webhook Result: " + targetSnapshot.toString());











    }

    public static void Log(String s) {

        System.out.println(s);
    }

}
