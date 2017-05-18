package com.nimbits.it.crud;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UpdateEntityCrudSync extends AbstractBaseNimbitsTest {

    @Test
    public void testSyncUpdates() {


        Point updated = adminClient.addPoint(adminUser, new PointModel.Builder()
                .create());

        assertNotNull(updated.getId());
        assertFalse(updated.isHighAlarmOn());

        updated.setHighAlarm(100.0);
        updated.setHighAlarmOn(true);

        Point result = (Point) adminClient.updateEntitySync(updated);
        assertTrue(result.isHighAlarmOn());



    }
}
