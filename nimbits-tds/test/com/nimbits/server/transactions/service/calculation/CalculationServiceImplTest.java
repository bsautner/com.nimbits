/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.service.calculation;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.calculation.CalculationService;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 7:14 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class CalculationServiceImplTest extends NimbitsServletTest {

    @Resource(name="commonFactory")
    CommonFactory common;

    @Resource(name="entityService")
    EntityService entityService;

    @Resource(name="valueService")
    ValueService valueService;

    @Resource(name="calculationService")
    CalculationService calculationService;

    private static final double DELTA = 0.0001;

    @Test
    public void testCalcs() throws NimbitsException, InterruptedException {


        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = common.createName(UUID.randomUUID().toString(), EntityType.point);


        //final EntityName cName = common.createName(UUID.randomUUID().toString(), EntityType.calculation);
        // Category c = ClientHelper.client().addCategory(cName);
        // assertNotNull(c);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);

        org.junit.Assert.assertNotNull(y);
        org.junit.Assert.assertNotNull(z);
        org.junit.Assert.assertNotNull(trigger);
        org.junit.Assert.assertNotNull(trigger.getKey());
        org.junit.Assert.assertNotNull(target);
        org.junit.Assert.assertNotNull(target.getKey());
        final Random r = new Random();
        final double r1 = r.nextDouble();
        final double r2 = r.nextDouble();
        final double ry = r.nextDouble();
        final double rz = r.nextDouble();

        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z+" + r1, target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        final Entity ce = entityService.addUpdateEntity(calculation);

        org.junit.Assert.assertNotNull(ce);

        final List<Entity> c =entityService.getEntityByKey(user, ce.getKey(), EntityType.calculation); 
        org.junit.Assert.assertNotNull(c);
        org.junit.Assert.assertFalse(c.isEmpty());

        entityService.addUpdateEntity(trigger);
      

        valueService.recordValue(user,yName, ValueFactory.createValueModel(ry));

        valueService.recordValue(user, zName, ValueFactory.createValueModel(rz));
        Thread.sleep(200);
        final Value vt = valueService.recordValue(user, triggerName,  ValueFactory.createValueModel(r2));

        org.junit.Assert.assertEquals(vt.getDoubleValue(), r2, DELTA);

        final  List<Value> vy = valueService.getCurrentValue(y);// ClientHelper.client().getCurrentRecordedValue(yName);
        org.junit.Assert.assertEquals(vy.get(0).getDoubleValue(), ry, DELTA);

        final  List<Value> vz = valueService.getCurrentValue(z);
        org.junit.Assert.assertEquals(vz.get(0).getDoubleValue(), rz, DELTA);

        Thread.sleep(1000);
        calculationService.processCalculations(user, trigger, vt);
        Thread.sleep(1000);
        final  List<Value> endResult = valueService.getCurrentValue(target);
        org.junit.Assert.assertNotNull(endResult);
        org.junit.Assert.assertEquals(r1 + r2 + ry + rz, endResult.get(0).getDoubleValue(), DELTA);

    }

    @Test(expected=NimbitsException.class)
    public void testCalcLoop() throws NimbitsException, InterruptedException {

        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = common.createName(UUID.randomUUID().toString(), EntityType.point);

        //final EntityName cName = common.createName(UUID.randomUUID().toString(), EntityType.calculation);
        // Category c = ClientHelper.client().addCategory(cName);
        // assertNotNull(c);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);

        org.junit.Assert.assertNotNull(y);
        org.junit.Assert.assertNotNull(z);
        org.junit.Assert.assertNotNull(trigger);
        org.junit.Assert.assertNotNull(trigger.getKey());
        org.junit.Assert.assertNotNull(target);
        org.junit.Assert.assertNotNull(target.getKey());


        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z", trigger.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        final Entity ce = entityService.addUpdateEntity(calculation);

        org.junit.Assert.assertNotNull(ce);

        final List<Entity> c =entityService.getEntityByKey(user, ce.getKey(), EntityType.calculation); 
        org.junit.Assert.assertNotNull(c);
        org.junit.Assert.assertFalse(c.isEmpty());

        entityService.addUpdateEntity(trigger); 

    }


    @Test(expected=NimbitsException.class)
    public void testCalcRecursion() throws NimbitsException, InterruptedException {

        final EntityName targetName = common.createName("TARGET", EntityType.point);
        final EntityName targetName2 = common.createName("TARGET2", EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER", EntityType.point);
        final EntityName triggerName2 = common.createName("TRIGGER2", EntityType.point);

        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);
        final Point target2 = addPoint(targetName2);
        final Point trigger2 = addPoint(triggerName2);

        org.junit.Assert.assertNotNull(trigger);
        org.junit.Assert.assertNotNull(trigger.getKey());
        org.junit.Assert.assertNotNull(target);
        org.junit.Assert.assertNotNull(target.getKey());


        EntityName name = common.createName("calc", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "1+1", target.getKey(),"","", "");

        entityService.addUpdateEntity(calculation);


        Entity en2 = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation2 = CalculationModelFactory.createCalculation(en2, target.getKey(),
                true, "1+1", target2.getKey(), "", "", "");

        entityService.addUpdateEntity(calculation2);

        Entity en3 = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation3 = CalculationModelFactory.createCalculation(en3,  target2.getKey(),
                true, "1+1", trigger.getKey(), "", "", "");

        entityService.addUpdateEntity(calculation3);

    }

    private Point addPoint(EntityName name) throws NimbitsException {
        Entity e =  new EntityModel(name,
                "",
                EntityType.point,
                ProtectionLevel.everyone,
                user.getKey(),
                user.getKey(),
                UUID.randomUUID().toString());
        Point p =    PointModelFactory.createPointModel(
                e,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0 );
        return (Point) entityService.addUpdateEntity(user, p);
    }

    @Test(expected=NimbitsException.class)
    public void testBadCalc1() throws NimbitsException, InterruptedException {


        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y = addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "X+y+z", target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        entityService.addUpdateEntity(calculation);


    }

    @Test(expected=NimbitsException.class)
    public void testBadCalcY() throws NimbitsException, InterruptedException {


        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+Y+z", target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        entityService.addUpdateEntity(calculation);


    }
    @Test(expected=NimbitsException.class)
    public void testBadCalcZ() throws NimbitsException, InterruptedException {


        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+Z", target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        entityService.addUpdateEntity(calculation);


    }
    @Test(expected=NimbitsException.class)
    public void testBadCalcMissingZ() throws NimbitsException, InterruptedException {


        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);



        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z", target.getKey(), trigger.getKey(), y.getKey(),"");

        entityService.addUpdateEntity(calculation);


    }

    @Test(expected=NimbitsException.class)
    public void testBadCalcMissingX() throws NimbitsException, InterruptedException {


        final EntityName targetName = common.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = common.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = common.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = common.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z", target.getKey(), "", y.getKey(),z.getKey());

        entityService.addUpdateEntity(calculation);


    }
}
