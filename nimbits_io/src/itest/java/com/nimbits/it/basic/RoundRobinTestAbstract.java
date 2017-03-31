package com.nimbits.it.basic;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RoundRobinTestAbstract extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void pointUpdateTest() {

        User me = nimbits.getMe(false).get();
        Point p1 = nimbits.addPoint(me, new PointModel.Builder()
                .highAlarmOn(true)
                .highAlarm(100.0)
                .lowAlarmOn(true)
                .lowAlarm(-100.0)
                .idleAlarmOn(true)

                .create());


        Point p2 = nimbits.getPoint(p1.getId());
        Point p3 = nimbits.getPoint(p1.getId());

        assertTrue(p1.equals(p2));
        assertTrue(p1.equals(p3));


        p1.setHighAlarmOn(false);
        p1.setLowAlarmOn(false);
        p1.setIdleAlarmOn(false);

        nimbits.updateEntity(p1);

        Point p4 = nimbits.getPoint(p1.getId());
        Point p5 = nimbits.getPoint(p1.getId());

        assertTrue(p1.equals(p4));
        assertTrue(p1.equals(p5));



    }

}
