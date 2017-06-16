package com.nimbits.it.basic;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.Calculation;
import com.nimbits.client.model.Entity;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CalcTest  extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {

        super.setUp();
    }

    @Test
    public void createCalcTest() {

        Topic trigger = nimbits.addPoint(user, new Topic.Builder()
                .name("BERNp1_" + UUID.randomUUID().toString())
                .create());

        Topic target1 = nimbits.addPoint(user, new Topic.Builder()
                .name("BERNp2_" + UUID.randomUUID().toString())
                .create());

        String ID = target1.getId();

        Topic target2 = nimbits.addPoint(user, new Topic.Builder()
                .name("BERNp3_" + UUID.randomUUID().toString())
                .create());



        List<Topic> targets = new ArrayList<>();
        targets.add(target1);
        targets.add(target2);


        Calculation calculation = nimbits.addCalc(user, new Calculation.Builder()
                .formula("[" + trigger.getName() + "]" + "*3")
                .trigger(trigger)
                .name("calc_" + UUID.randomUUID().toString())
                .targets(targets)
                .execute(true)
                .enabled(true)
                .create());

        log("formula", calculation.getFormula());
        log("target1: " + target1.getId());

        sleep();
        double testValue3 = new Random().nextDouble() * 100;
        log("sending into calc: " + testValue3);

        nimbits.recordValue(trigger, new Value.Builder().doubleValue(testValue3).create());


        Optional<? extends Entity> calc = nimbits.getEntity(EntityType.calculation, calculation.getId());

        assertTrue(calc.isPresent());
        Calculation c = (Calculation) calc.get();
        assertEquals(c.getTargets().size(), targets.size());
        for (Topic topic : c.getTargets()) {

            log(topic.getName(), topic.getId());

            if (topic.getName().equals(target1.getName())) {
                assertEquals(ID, topic.getId());
            }


        }

        sleep();

        Value value1 = nimbits.getSnapshot(target1);
        Value value2 = nimbits.getSnapshot(target2);
        assertEquals(testValue3*3, value1.getDoubleValue(), 0.001);
        assertEquals(testValue3*3, value2.getDoubleValue(), 0.001);




    }

}
