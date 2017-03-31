package com.nimbits.it.basic;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdlePointTestAbstract extends AbstractNimbitsTest {


    @Before
    public void setup() {

        List<Entity> entityList = nimbits.getChildren(nimbits.getMe().get());
        for (Entity e : entityList) {
            nimbits.deleteEntity(e);
        }


    }
    @After
    public void tearDown() {

    }

    @Test
    public void testGettingIdlePoints() {

        User me = nimbits.getMe(false).get();
        List<Point> idlePoints = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            idlePoints.add(nimbits.addPoint(me, new PointModel.Builder()

                    .idleAlarmOn(true)
                    .idleSeconds(30)
                    .create()));
            log("Creating idle point " + i);

        }
        sleep();
        for (Point e : idlePoints) {
            nimbits.recordValue(e, new Value.Builder().data(UUID.randomUUID().toString()).create());
            log("recording a value to " + e.getName());
        }
        sleep();

        for (Point e : idlePoints) {
            log("Verifying " + e.getName());
            Point r = nimbits.getPoint(e.getId());
            assertTrue(r.isIdleAlarmOn());
            assertFalse(r.idleAlarmSent());



        }



    }

    @Test
    public void testTimeStamp() {

        System.out.println(System.currentTimeMillis());


    }

}
