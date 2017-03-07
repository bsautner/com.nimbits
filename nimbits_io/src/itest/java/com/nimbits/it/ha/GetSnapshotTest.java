package com.nimbits.it.ha;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.NimbitsTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 *  getSnapshot vs getValues.
 Short description: Java API getSnapshot doesn't return the last value recorded on a datapoint.

 Scenario:
 Create datapoint on one of the instances.
 Check that it is visible from another instance (either Web UI or REST)
 Make several records to the datapoint from "n1".
 Read datapoint snapshot from "n1" and "n2" either using Java API or Rest '/snapshot'.
 [Expected]: Snapshot values expected to be the same on both "n1" and "n2".
 [Actual]: Snapshot value on "n2" will not show actual (the last) value.

 It is observed, that Java API function getValues(..., 1) returns actual value.

 */
public class GetSnapshotTest extends NimbitsTest {


    @Test
    public void testSnapshots() {

        User me = nimbits.getMe();
        Point p = nimbits.addPoint(me, new PointModel.Builder().create());


        //TEST round robin - point found on all instances.

        for (int i = 0; i < 10; i++) {
            assertTrue(nimbits.findPointByName(p.getName().getValue()).isPresent());
        }

        Value value;
        for (int i = 0; i <= 10; i++) {
            value = new Value.Builder().doubleValue(Double.valueOf(String.valueOf(i))).create();
            nimbits.recordValueSync(p.getName().getValue(), value);

        }

        for (int i = 0; i < 10; i++) {
           Value snap = nimbits.getSnapshot(p);
           assertEquals(10, snap.getDoubleValue(), 0.0001);

        }
        log(p);

    }
}
