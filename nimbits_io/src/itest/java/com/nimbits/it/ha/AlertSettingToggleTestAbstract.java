package com.nimbits.it.ha;


import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Change alert settings.
 Short description: any changes on alert settings in one of the instances don't get into another.

 Scenario:
 Create datapoint on one of the instances.
 Check that it is visible from another instance (either Web UI or REST)
 Change any alert setting (high/low/idle value/enabled) either using WebUI or Java API  on datapoint from one of the instances.
 Read alert settings from another instance (either Web UI or REST).
 [Expected]: Alert settings expected to be the same.
 [Actual]: settings on another nimbits are not changed.
 */
public class AlertSettingToggleTestAbstract extends AbstractNimbitsTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testChangeAlerts() {

        int count = 10;
        List<Point> pointList = new ArrayList<>(count);
        User me = nimbits.getMe().get();

        for (int c = 0; c < count; c++) {

            pointList.add(nimbits.addPoint(me,
                    new PointModel.Builder().create()));


        }

        for (Point point : pointList) {
            assertFalse(point.isHighAlarmOn());
            point.setHighAlarmOn(true);
            nimbits.updateEntity(point);
            nap();
            Point update = nimbits.getPoint(point.getId());
            assertTrue(update.isHighAlarmOn());
        }





    }

}
