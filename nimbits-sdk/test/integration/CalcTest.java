package integration;

import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/19/11
 * Time: 4:06 PM
 */
public class CalcTest {

    @Test
    public void testCalc1() throws IOException, InterruptedException {

        //TODO pass test

        EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString());
        EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString());

        EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());
        EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());


       EntityName cName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());
      // Category c = ClientHelper.client().addCategory(cName);
      // assertNotNull(c);
        Point trigger = ClientHelper.client().addPoint(triggerName);
        Point target = ClientHelper.client().addPoint(targetName);

        Point y = ClientHelper.client().addPoint( yName);
        Point z = ClientHelper.client().addPoint(zName);

        assertNotNull(trigger);
        assertTrue(trigger.getId() > 0);
        assertNotNull(target);
        assertTrue(target.getId() > 0);
        Random r = new Random();

        double r1 = r.nextDouble();
        double r2 = r.nextDouble();
        double ry = r.nextDouble();
        double rz = r.nextDouble();

        Calculation calculation = CalculationModelFactory.createCalculation(trigger.getUUID(), UUID.randomUUID().toString(),
        true, "x+y+z+" + r1, target.getUUID(), trigger.getUUID(), y.getUUID(), z.getUUID());


        ClientHelper.client().addCalculation(calculation, cName);
//
//        Calculation calc = PointModelFactory.createCalculation(true, "x+y+z+" + r1, target.getUUID(), trigger.getUUID(), y.getUUID(), z.getUUID());
//        trigger.setCalculation(calc);

//        trigger.setX(trigger.getId());
//        trigger.setY(y.getId());
//        trigger.setZ(z.getId());
//        trigger.setFormula();
//
//        trigger.setTarget(target.getId());
        ClientHelper.client().updatePoint(trigger);

        ClientHelper.client().recordValue(yName, ry);
        ClientHelper.client().recordValue(zName, rz);
        Thread.sleep(100);
        ClientHelper.client().recordValue(triggerName, r2);
        Value vt = ClientHelper.client().getCurrentRecordedValue(triggerName);
        assertEquals(vt.getDoubleValue(), r2, 0.0001);

        Value vy = ClientHelper.client().getCurrentRecordedValue(yName);
        assertEquals(vy.getDoubleValue(), ry, 0.0001);

        Value vz = ClientHelper.client().getCurrentRecordedValue(zName);
        assertEquals(vz.getDoubleValue(), rz, 0.0001);

        Thread.sleep(100);
        Value endResult = ClientHelper.client().getCurrentRecordedValue(targetName);

        assertEquals(r1 + r2 + ry + rz, endResult.getDoubleValue(), 0.001);


    }



}
