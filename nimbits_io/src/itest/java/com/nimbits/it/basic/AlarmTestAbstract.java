package com.nimbits.it.basic;

import com.google.common.base.Optional;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.UUID;

import static org.junit.Assert.*;

public class AlarmTestAbstract extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeIdleSetTest() throws InterruptedException {
        doTest(true, true, true);
        doTest(true, true, false);
        doTest(true, false, true);
        doTest(false, true, true);


        doTest(true, false, false);
        doTest(false, true, false);
        doTest(false, false, true);

        doTest(false, false, false);

    }

    private void doTest(boolean idleSetting, boolean lowSetting, boolean highSetting) throws InterruptedException {

        String pointName = UUID.randomUUID().toString();

        final boolean[] gotCallback = {false};

        Point point;
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                gotCallback[0] = true;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fail();
                retrofitError.printStackTrace();
            }
        };

        nimbits.addPoint(user, new PointModel.Builder().name(pointName).create());

        Optional<Point> serversVersion;

        serversVersion = nimbits.findPointByName(pointName);
        assertTrue(serversVersion.isPresent());
        assertNotNull(serversVersion.get().getId());
        point = serversVersion.get();
        assertEquals(pointName, point.getName().getValue());

        point.setIdleAlarmOn(idleSetting);
        point.setLowAlarmOn(lowSetting);
        point.setHighAlarmOn(highSetting);

        assertTrue(point.isIdleAlarmOn() == idleSetting);
        assertTrue(point.isLowAlarmOn() == lowSetting);
        assertTrue(point.isHighAlarmOn() == highSetting);


        nimbits.updateEntity(point, callback);

        while (! gotCallback[0]) {
            Thread.sleep(1); //wait for callback
        }
        serversVersion = nimbits.findPointByName(pointName);
        assertTrue(serversVersion.isPresent());
        point = serversVersion.get();
        assertEquals(idleSetting, point.isIdleAlarmOn());
        assertEquals(lowSetting, point.isLowAlarmOn());
        assertEquals(highSetting, point.isHighAlarmOn());


    }


}
