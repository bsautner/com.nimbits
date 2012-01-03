import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Calculation;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.point.PointName;
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
        PointName targetName = CommonFactoryLocator.getInstance().createPointName("TARGET" + UUID.randomUUID().toString());
        PointName triggerName = CommonFactoryLocator.getInstance().createPointName("TRIGGER" + UUID.randomUUID().toString());

        PointName yName = CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString());
        PointName zName = CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString());


        CategoryName cName = CommonFactoryLocator.getInstance().createCategoryName(UUID.randomUUID().toString());
        Category c = ClientHelper.client().addCategory(cName);
        assertNotNull(c);
        Point trigger = ClientHelper.client().addPoint(c.getName(), triggerName);
        Point target = ClientHelper.client().addPoint(c.getName(), targetName);

        Point y = ClientHelper.client().addPoint(c.getName(), yName);
        Point z = ClientHelper.client().addPoint(c.getName(), zName);

        assertNotNull(trigger);
        assertTrue(trigger.getId() > 0);
        assertNotNull(target);
        assertTrue(target.getId() > 0);
        Random r = new Random();

        double r1 = r.nextDouble();
        double r2 = r.nextDouble();
        double ry = r.nextDouble();
        double rz = r.nextDouble();
        Calculation calc = PointModelFactory.createCalculation(true, "x+y+z+" + r1, target.getId(), trigger.getId(), y.getId(), z.getId());
        trigger.setCalculation(calc);

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
        assertEquals(vt.getNumberValue(), r2, 0.0001);

        Value vy = ClientHelper.client().getCurrentRecordedValue(yName);
        assertEquals(vy.getNumberValue(), ry, 0.0001);

        Value vz = ClientHelper.client().getCurrentRecordedValue(zName);
        assertEquals(vz.getNumberValue(), rz, 0.0001);

        Thread.sleep(100);
        Value endResult = ClientHelper.client().getCurrentRecordedValue(targetName);

        assertEquals(r1 + r2 + ry + rz, endResult.getNumberValue(), 0.001);


    }

}
