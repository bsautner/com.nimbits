package com.nimbits.it.basic;

import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

//Failing
public class ExpireTest extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void executeIdleSetTest() throws InterruptedException {

        String pointName = UUID.randomUUID().toString();
        int c = 100;
        int e = 9000;


        Topic topic = nimbits.addPoint(user, new Topic.Builder()
                .name(pointName).expire(e).create());

        Random random = new Random();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, e * -1);
        for (int i = 0; i < c; i++) {


            nimbits.recordValueSync(pointName, new Value.Builder().timestamp(calendar.getTimeInMillis() - random.nextInt(10000000)).doubleValue(random.nextDouble() * 100).create());

            log("Recording Synchronously " + i);
        }

        sleep();

        List<Value> valueList = nimbits.getValues(topic, c);
        for (Value vx : valueList) {
            log(vx);
        }
        assertEquals(0, valueList.size());



        for (int i = 0; i < c; i++) {


            nimbits.recordValueSync(pointName, new Value.Builder().timestamp(System.currentTimeMillis()-c).doubleValue(random.nextDouble() * 100).create());

        }
        sleep(5);

        List<Value> valueList1 = nimbits.getValues(topic, c);
        for (Value vx : valueList1) {
            log(vx.getDoubleValue() + vx.getTimestamp().toString());
        }
        assertEquals(c, valueList1.size());


    }
}
