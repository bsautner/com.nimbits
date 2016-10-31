package com.nimbits.it;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import org.junit.Before;
import org.junit.Test;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SnapshotTest extends NimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void testPostSnapshot() throws InterruptedException {
        final String pointName = UUID.randomUUID().toString();



        nimbits.addPoint(user, new PointModel.Builder().name(pointName).create());

        nimbits.setSnapshot(pointName, new Value.Builder().timestamp(0L).doubleValue(11.0).create(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fail();
            }
        });


        Thread.sleep(1000);
        Value v = nimbits.getSnapshot(pointName);
        assertEquals(11.0, v.getDoubleValue(), 0.01);
        assertTrue(v.getLTimestamp() > 0);



    }
}
