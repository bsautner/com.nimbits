package com.nimbits.it.basic;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class HighFrequencyRepeaterTestAbstract extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeIdleSetTest() throws InterruptedException {

        String pointName = UUID.randomUUID().toString();
        int c = 100;


        Point point = nimbits.addPoint(user, new PointModel.Builder().name(pointName).filterType(FilterType.fixedHysteresis).filterValue(0.5).create());
        double v = 0.0;


        for (int i = 0; i < c; i++) {

            if (v == 1.0) {
                v = 0.0;
            } else {
                v = 1.0;
            }
            nimbits.recordValueSync(pointName, new Value.Builder().doubleValue(v).create());





        }

        sleep(5);

        List<Value> valueList = nimbits.getValues(point, c);
        for (Value vx : valueList) {
            log(vx.getDoubleValue());
        }
        assertEquals(c, valueList.size());




    }
}
