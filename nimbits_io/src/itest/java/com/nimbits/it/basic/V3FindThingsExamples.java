package com.nimbits.it.basic;

import com.google.common.base.Optional;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;
import retrofit.RetrofitError;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class V3FindThingsExamples extends AbstractNimbitsTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeTest() throws InterruptedException {


        String pointName = UUID.randomUUID().toString();

        //create a point under the top level user with a random name
        Point point = new PointModel.Builder().name(pointName).parent(user.getId())
                .highAlarmOn(true)
                .highAlarm(100.00)
                .lowAlarmOn(true)
                .lowAlarm(0.0)
                .create();
        Entity newPoint = nimbits.addPoint(user, point);
        log("Created : " + newPoint.getName().getValue());

        Optional<Point> foundPoint = nimbits.findPointByName(pointName);
        if (foundPoint.isPresent()) {
            log("verified point");
            if (!foundPoint.get().isHighAlarmOn() || !foundPoint.get().isLowAlarmOn()) {
                throw new RuntimeException(" Alarm was off when it was set to on!");

            }
        } else {
            throw new RuntimeException("Point not found after being created and searched for");
        }

        //search for a point that was never created to test absent condition
        try {
            Optional<Point> shouldNotExist = nimbits.findPointByName(UUID.randomUUID().toString());

        } catch (RetrofitError error) {
            assertEquals(404, error.getResponse().getStatus());
        }


        //Record some values with the name only
        double testValue = 100.00 * new Random().nextDouble();
        Value value = new Value.Builder().doubleValue(testValue).create();
        nimbits.recordValue(pointName, value);
        Thread.sleep(1000);
        Value retrieved = nimbits.getSnapshot(pointName);
        assertEquals(value.getDoubleValue(), retrieved.getDoubleValue(), 0.001);


        veryifyFindCategory();
        veryifyFindWebHook();

    }

    private void veryifyFindCategory() {
        String name = UUID.randomUUID().toString();

        try {
            Optional<Category> result = nimbits.findCategory("i dont exist");

        } catch (RetrofitError error) {
            assertEquals(404, error.getResponse().getStatus());
        }


        Category category = new CategoryModel.Builder().name(name).create();
        nimbits.addCategory(user, category);

        Optional<Category> verify = nimbits.findCategory(name);
        assertTrue(verify.isPresent());
    }

    private void veryifyFindWebHook() {
        String name = UUID.randomUUID().toString();


        try {
         Optional<WebHook> result = nimbits.findWebHook("i dont exist");

        } catch (RetrofitError error) {
            assertEquals(404, error.getResponse().getStatus());
        }

        WebHook webHook = new WebHookModel.Builder()
                .name(name)
                .method(HttpMethod.GET)
                .url("http://www.foo.com")
                .create();
        nimbits.addWebHook(user, webHook);

        Optional<WebHook> verify = nimbits.findWebHook(name);
        assertTrue(verify.isPresent());
    }


}



