package com.nimbits.it;

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
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class V3FindThingsExamples extends NimbitsTest{


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
        Entity newPoint =  nimbits.addPoint(user, point);
        log("Created : " + newPoint.getName().getValue());

        Optional<Point> foundPoint = nimbits.findPointByName(pointName);
        if (foundPoint.isPresent()) {
            log("verified point");
            if (! foundPoint.get().isHighAlarmOn() || ! foundPoint.get().isLowAlarmOn()) {
                throw new RuntimeException(" Alarm was off when it was set to on!");

            }
        }
        else {
            throw new RuntimeException("Point not found after being created and searched for");
        }

        //search for a point that was never created to test absent condition
        Optional<Point> shouldNotExist = nimbits.findPointByName(UUID.randomUUID().toString());
        if (shouldNotExist.isPresent()) {
            throw new RuntimeException("Point found that was never created!");
        }
        else {
            log("verified absent point");
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

        Optional<Category> result = nimbits.findCategory("i don't exist");
        verifyReponse(result, false);

        Category category = new CategoryModel.Builder().name(name).create();
        nimbits.addCategory(user, category);

        Optional<Category> verify = nimbits.findCategory(name);
        verifyReponse(verify, true);
    }

    private void veryifyFindWebHook() {
        String name = UUID.randomUUID().toString();

        Optional<WebHook> result = nimbits.findWebHook("i don't exist");
        verifyReponse(result, false);

        WebHook webHook = new WebHookModel.Builder()
                .name(name)
                .method(HttpMethod.GET)
                .url("http://www.foo.com")
                .create();
        nimbits.addWebHook(user, webHook);

        Optional<WebHook> verify = nimbits.findWebHook(name);
        verifyReponse(verify, true);
    }

    private void verifyReponse(Optional result, boolean shouldExist) {

      //  assertEquals(! shouldExist, result.isPresent());


        if (! shouldExist && result.isPresent()) {
            throw new RuntimeException("Found an object that should not exist!");
        }
        else if (shouldExist && ! result.isPresent()) {
            throw new RuntimeException("Did NOT find an object that should exist!");
        }
        else {

            log("Got expected result looking for  entity");

        }
    }
}



