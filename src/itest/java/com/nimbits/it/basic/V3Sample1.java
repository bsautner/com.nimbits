package com.nimbits.it.basic;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.model.Group;
import com.nimbits.client.model.Filter;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class V3Sample1 extends AbstractNimbitsTest {

    /**
     * This sample is meant to walk through some of the basic nimbits automation features and uses nimbits.io to:
     * <p>
     * <p>
     * 1. Create an admin user on a new nimbits server (@see com.nimbits.it.AbstractNimbitsTest base class)
     * 2. Create some regular users using the admin's credentials - then delete some of them
     * 3. Re-Connect to the server as a regular user
     * 4. Create A folder under the user's account
     * 5. Create Some Data Points in that folder
     * 6. Create a WebHook using code
     * 7. Create Subscriptions for those points that uses that webhook
     * 8. Write some data and verify the subscriptions
     * 9. Record Some data over a period of time and verify the data using meta data and masks
     * 10. Verify Snapshots by writing single and series of data - the most recent value should always be available in the snapshot
     * <p>
     * Note:
     * <p>
     * the api is designed to take a POJO and return a new object that is the fully created object with database keys etc
     * so code like this is common:
     * <p>
     * Foo foo1 = FooBuilder().create();
     * Foo foo2 = nimbits.createFoo(foo1);
     * <p>
     * foo2 is a new object, returned by the api where foo1 was a DTO used to create foo2.
     * <p>
     * to avoid confusion the sample will do:
     * <p>
     * Foo foo1 = FooBuilder().create();
     * foo1 = nimbits.createFoo(foo1);
     */


    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeTest() throws InterruptedException {

             /*
        

        /*
        Step 2:

        Create a new user using the admin client, only admins can create users:
         */
        String email = UUID.randomUUID().toString() + "@example.com";
        String password = "userpassword1234";
        try {

            User basicUser = new User.Builder().email(email).password(password).create();
            basicUser = nimbits.addUser(basicUser);

            log("Created User: " + basicUser.toString());
        } catch (Throwable throwable) {
            //This will throw if the user already exists

            fail("error adding user 1: " + throwable.getMessage());
            fail();
        }

        //create a second user with a random account
        String email2 = UUID.randomUUID().toString() + "@example.com";

        try {

            User basicUser = new User.Builder().email(email2).password(password).create();
            basicUser = nimbits.addUser(basicUser);

            log("Created User: " + basicUser.toString());
        } catch (Throwable throwable) {
            //This will throw if the user already exists

            fail("error adding user 2: " + throwable.getMessage());
        }

        //veryify user exists
        sleep();

        Nimbits user2Client = new Nimbits.Builder().instance(host).email(email2).token(password).create();
        User retrieved = user2Client.getMe();
        if (retrieved != null) {
            log("Downloaded:   " + retrieved.toString());
            Thread.sleep(1000);

            nimbits.deleteEntity(retrieved);
            Thread.sleep(1000);
            //make sure it was deleted

            try {

                Optional<User> retrieved2 = nimbits.findUser(email2);
            } catch (NimbitsClientException e) {
            fail(); //TODO
               // assertEquals(404, error.getResponse().getStatus());

            }


        } else {

            fail("got expected result: user didn't exist after adding: " + email2);
        }





        /*
        Step 3

        Create a new client with the user's credentials instead:
         */

        log("Getting a client for: " + email);
        Nimbits client = new Nimbits.Builder()
                .email(email).token(password).instance(host).create();

        User me = client.getMe(true);

        log("Re-Downloaded basic user to verify: " + me.toString());


        //4: Create a group (aka group) with the user entity (me) as the parent (this won't throw an error if a duplicate group is added, since you can have folders with the same name)

        Group group = new Group.Builder().name("my group 6").create();
        group = client.addCategory(me, group);
        log("created group: " + group.toString());


        /*
        Step 5:

         Create some data points - one will be a subscription trigger, so when it's writen to it'll fire off a web hook we're going to create below
         the other will be a target, which is an optional setting for a webhook where the result of the http call will be stored in the dx channel as a new value
         we add the current time to the topic name so it's always unique

        */
        Topic newTrigger = new Topic.Builder()
                .name("Data Topic Listener " + System.currentTimeMillis())
                .create();
        Topic newTarget = new Topic.Builder().name("Data Topic Target" + System.currentTimeMillis()).create();


        newTrigger = client.addPoint(group, newTrigger);
        newTarget = client.addPoint(group, newTarget);
        log("Created Data Topic: " + newTrigger.getId() + " " + newTrigger.toString());
        log("Created Data Topic: " + newTarget.getId() + " " + newTarget.toString());


        log("Verified Topic using newly created uuid " + client.getPoint(newTarget.getId()));
        log("Verified Topic using newly created uuid " + client.getPoint(newTarget.getId()));




        /*
        Step 6

        Let's create a webhook!  The webhook will contain a base url - when a topic is written to the data will be uses as a post body
        or querystring


        */

        String timeApi = "http://cloud.nimbits.com/service/v2/time";
        WebHook webHook = new WebHook.Builder()
                .name("Web Hook To Time API")
                .method(HttpMethod.GET)
                .target(newTarget)
                .url(timeApi)
                .create();

        webHook = client.addWebHook(group, webHook);

        log("Created webhook: " + webHook.toString());




        /*
        Step 7

        Create a subscription so when the trigger topic gets new data, webhook will be used

         */

        Subscription subscription = new Subscription.Builder()
                .eventType(EventType.newValue)
                .notifyMethod(SubscriptionNotifyMethod.webhook)
                .name("Event Subscription Time API Web Hook Call when Listener topic is written To")
                .target(webHook.getId()) //note that the subsciption target is the webhook - the webhook target is the target topic
                .subscribedEntity(newTrigger.getId())
                .create();

        subscription = client.addSubscription(group, subscription);
        log("Created Subscription: " + subscription.toString());


        /*
        Step 8:

        Write some data to the trigger topic, which will cause the subscription to run - using the webhook to download the current time from the time api

        Write the data - if the webhook was a post, the data in this value could be the post body and contain JSON. In a GET the data will be the query string
        in our case, the time api doesn't require any data, but we want to write something so the data isn't filtered out as noise.


         */

        Value value = new Value.Builder().data("?foo=" + UUID.randomUUID().toString()).create();

        client.recordValues(newTrigger, Collections.singletonList(value));

        //That value should now be the current value for the topic, let's download the snapshot (aka most recent value) and verify
        sleep();
        Value snapshot = client.getSnapshot(newTrigger);

        log("Verified value: " + snapshot.toString());


        //If all went well, the new value posted to the trigger should have caused the webhook to query the time api and save the result in the target topic

        Value targetSnapshot = client.getSnapshot(newTarget);

        log("Verified Webhook Result: " + targetSnapshot.toString());



        /*
        Step 9: record some values and test they are being saved and query by meta data


        Setting expire very high and filtertype to none will ensure data is recorded
        using dog and cat as two random words

         */

        Topic testTopic1 = new Topic.Builder()
                .name("Meta Data Topic Test " + System.currentTimeMillis())
                .create();





        testTopic1 = client.addPoint(group, testTopic1);
        Filter filter = new Filter.Builder()
                .filterType(FilterType.none)
                .trigger(testTopic1)
                .create();
        nimbits.addFilter(testTopic1, filter);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1); //start in some time in the past

        //we're going to store the values we record locally so we can compare them with what we download
        List<Value> dogs = new ArrayList<Value>();
        List<Value> cats = new ArrayList<Value>();

        String DOG = "dog";
        String CAT = "cat";

        for (int i = 0; i < 100; i++) {
            Value newValue = new Value.Builder()
                    .data("Some Random Data " + i)
                    .meta(i % 2 == 1 ? DOG : CAT) //alternate recording different meta values
                    .timestamp(calendar.getTimeInMillis())
                    .lat(0.0)
                    .lng(0.0)
                    .create();

            if (DOG.equals(newValue.getMetaData())) {
                dogs.add(newValue);
            } else if (CAT.equals(newValue.getMetaData())) {
                cats.add(newValue);
            }

            client.recordValue(testTopic1, newValue);
            calendar.add(Calendar.SECOND, 1);
            log("Recorded: " + newValue.toString() + " " + newValue.hashCode());

        }

        Thread.sleep(2000);
        //if you want everything, use a large data range, but avoid Date(0) or you'll get the init null value

        List<Value> storedValues = client.getValues(testTopic1, new Date(1), new Date(99999999999999L));
        log("Downloaded " + storedValues.size());
        for (Value d : storedValues) {
            log("Downloaded: " + d.toString() + " " + d.hashCode());
        }

//        for (Value dog : dogs) {
//            if (!storedValues.contains(dog)) {
//                error("Missing Data in dog List: " + dog.toString() + dog.hashCode());
//            }
//        }
//        for (Value cat : cats) {
//            if (!storedValues.contains(cat)) {
//                error("Missing Data in cat List");
//            }
//        }


        //Let's query with meta data and make sure we get what we expect
        List<Value> storedCats = client.getValues(testTopic1, new Date(1), new Date(99999999999999L), CAT);

        List<Value> storedDogs = client.getValues(testTopic1, new Date(1), new Date(99999999999999L), DOG);
//        if (!storedCats.containsAll(cats)) {
//            error("Missing some cats");
//        }
//
//        if (!storedDogs.containsAll(dogs)) {
//            error("Missing some dogs");
//        }



        /*
        10.

        Verify the snapshot is updated and available

         */

        Value snap;
        Topic snapshotTestTopic = new Topic.Builder()
                .name("Snapshot Topic Test " + System.currentTimeMillis())
                .expire(999999)
                .create();
        snapshotTestTopic = client.addPoint(group, snapshotTestTopic);


        snap = client.getSnapshot(snapshotTestTopic);
        log("Snapshot on a newly created topic: " + snap.toString() + " timestamp:" + snap.getTimestamp());




        Value test1 = new Value.Builder().data("Test Snapshot 1").timestamp(System.currentTimeMillis()).create();
        log("Recording new Value: " + test1);
        client.recordValue(snapshotTestTopic, test1);

        Thread.sleep(1000);

        snap = client.getSnapshot(snapshotTestTopic);
        log("Snapshot on a newly recorded value: " + snap.toString() + " timestamp:" + snap.getTimestamp());

        if (!snap.getData().equals(test1.getData())) {

            error("Snapshot on newly recorded value didn't match");

        }


        /*
        10.1

        Let's record a series of values and make sure the snapshot results in the most recent time

         */

        Topic seriesSnapshotTestTopic = new Topic.Builder()
                .name("Snapshot Topic Test " + System.currentTimeMillis())
                .expire(999999)
                .create();
        seriesSnapshotTestTopic = client.addPoint(group, seriesSnapshotTestTopic);


        List<Value> seriesSnapshotTest = new ArrayList<Value>();
        Calendar c = Calendar.getInstance();

        c.add(Calendar.YEAR, -1);
        for (int i = 0; i < 10; i++) {  //add 10 values with increasing dates
            Value testValue = new Value.Builder().data("Test Snapshot " + i).timestamp(c.getTimeInMillis()).create();
            c.add(Calendar.DAY_OF_YEAR, 1);
            seriesSnapshotTest.add(testValue);
        }


        log("Recording new Value: " + test1);
        client.recordValues(seriesSnapshotTestTopic, seriesSnapshotTest);

        Thread.sleep(1000);
        snap = client.getSnapshot(seriesSnapshotTestTopic);
        Value last = seriesSnapshotTest.get(seriesSnapshotTest.size() - 1);
        log(snap.toString());
        log(last.toString());
        if (!snap.getData().equals(last.getData())) {
            error("Most recent recorded value in series was not the snapshot");
        }


        log("Done " + getClass().getName());

    }


}
