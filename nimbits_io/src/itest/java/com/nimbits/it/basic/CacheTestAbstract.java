package com.nimbits.it.basic;

import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CacheTestAbstract extends AbstractNimbitsTest {

        @Before
        public void setUp() throws Exception {
            super.setUp();


        }

        @Test
        public void executeTest() throws InterruptedException {

            double t = 54;
            String name = UUID.randomUUID().toString();

            User me = nimbits.getMe(true);

            log(me.toString());

            nimbits.addPoint(me, new PointModel.Builder().name(name).create());

            assertTrue(nimbits.findPointByName(name).isPresent());

            nimbits.recordValue(name, new Value.Builder().data("debugging1").doubleValue(t).create());

            sleep();

            double r = nimbits.getSnapshot(name).getDoubleValue();

            assertEquals(t, r, 0.001);

            for (int i = 0; i < 3; i++) {
                log(nimbits.getSnapshot(name));
            }



        }


}
