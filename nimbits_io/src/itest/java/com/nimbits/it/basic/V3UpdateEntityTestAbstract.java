package com.nimbits.it.basic;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.it.AbstractNimbitsTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Before;
import org.junit.Test;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class V3UpdateEntityTestAbstract extends AbstractNimbitsTest {

    private CountDownLatch lock = new CountDownLatch(1);


    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void testSetup() {
        assertTrue(true);
    }

    @Test
    public void executeTest() throws InterruptedException {

        final String name = UUID.randomUUID().toString();
        Subscription subscription = new SubscriptionModel.Builder().name(name)
                .target("foo").create();
        Gson gson = GsonFactory.getInstance(true);
        String json = gson.toJson(subscription);
        log(json);
        Entity entity = gson.fromJson(json, Entity.class);
        Subscription subscription1 = (Subscription) entity;
        assertNotNull(subscription1.getTarget());

        assertEquals(name, entity.getName().getValue());
        nimbits.addSubscription(user, subscription);

        Optional<Subscription> returnedSubscription = nimbits.findSubscription(name);
        assertTrue(returnedSubscription.isPresent());

        //update it
        Subscription r1 = returnedSubscription.get();
        assertEquals(name, r1.getName().getValue());
        r1.setTarget("bar");

        nimbits.updateEntity(r1, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                lock.countDown();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fail(retrofitError.getMessage());
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        log("checking");
        Optional<Subscription> secondReturned = nimbits.findSubscription(name);
        assertTrue(secondReturned.isPresent());

        Subscription r2 = secondReturned.get();
        assertEquals("bar", r2.getTarget());


    }


}

