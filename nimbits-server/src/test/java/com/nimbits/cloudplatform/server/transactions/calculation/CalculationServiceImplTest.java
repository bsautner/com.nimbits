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

package com.nimbits.cloudplatform.server.transactions.calculation;


import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.calculation.CalculationModelFactory;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
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
 


    private static final double DELTA = 0.0001;

    @Test
    public void testCalcs() throws Exception {


        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);


        //final EntityName cName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.calculation);
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
        org.junit.Assert.assertNotNull(EntityModelFactory.createTarget(target.getKey()));
        final Random r = new Random();
        final double r1 = r.nextDouble();
        final double r2 = r.nextDouble();
        final double ry = r.nextDouble();
        final double rz = r.nextDouble();

        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "x+y+z+" + r1, EntityModelFactory.createTarget((target.getKey())), (trigger.getKey()), y.getKey(), z.getKey());

        final Entity ce = EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);

        org.junit.Assert.assertNotNull(ce);

        final List<Entity> c = EntityServiceFactory.getInstance().getEntityByKey(user, ce.getKey(), EntityType.calculation);
        org.junit.Assert.assertNotNull(c);
        org.junit.Assert.assertFalse(c.isEmpty());

        EntityServiceFactory.getInstance().addUpdateSingleEntity(trigger);


        ValueServiceFactory.getInstance().recordValue(user, yName, ValueFactory.createValueModel(ry));

        ValueServiceFactory.getInstance().recordValue(user, zName, ValueFactory.createValueModel(rz));
        Thread.sleep(200);
        final Value vt = ValueServiceFactory.getInstance().recordValue(user, triggerName, ValueFactory.createValueModel(r2));

        org.junit.Assert.assertEquals(vt.getDoubleValue(), r2, DELTA);

        final  List<Value> vy = ValueServiceFactory.getInstance().getCurrentValue(y);// ClientHelper.client().getCurrentRecordedValue(yName);
        org.junit.Assert.assertEquals(vy.get(0).getDoubleValue(), ry, DELTA);

        final  List<Value> vz = ValueServiceFactory.getInstance().getCurrentValue(z);
        org.junit.Assert.assertEquals(vz.get(0).getDoubleValue(), rz, DELTA);

        Thread.sleep(1000);
        CalculationTransaction.processCalculations(user, trigger);
        Thread.sleep(1000);
        final  List<Value> endResult = ValueServiceFactory.getInstance().getCurrentValue(target);
        org.junit.Assert.assertNotNull(endResult);
        org.junit.Assert.assertEquals(r1 + r2 + ry + rz, endResult.get(0).getDoubleValue(), DELTA);

    }

    @Test(expected=Exception.class)
    public void testCalcLoop() throws Exception {

        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);

        //final EntityName cName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.calculation);
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
        org.junit.Assert.assertNotNull(EntityModelFactory.createTarget(target.getKey()));


        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "x+y+z", EntityModelFactory.createTarget(trigger.getKey()), trigger.getKey(), y.getKey(), z.getKey());

        final Entity ce = EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);

        org.junit.Assert.assertNotNull(ce);

        final List<Entity> c = EntityServiceFactory.getInstance().getEntityByKey(user, ce.getKey(), EntityType.calculation);
        org.junit.Assert.assertNotNull(c);
        org.junit.Assert.assertFalse(c.isEmpty());

        EntityServiceFactory.getInstance().addUpdateSingleEntity(trigger);

    }


    @Test(expected=Exception.class)
    public void testCalcRecursion() throws Exception {

        final EntityName targetName = CommonFactory.createName("TARGET", EntityType.point);
        final EntityName targetName2 = CommonFactory.createName("TARGET2", EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER", EntityType.point);
        final EntityName triggerName2 = CommonFactory.createName("TRIGGER2", EntityType.point);

        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);
        final Point target2 = addPoint(targetName2);
        final Point trigger2 = addPoint(triggerName2);

        org.junit.Assert.assertNotNull(trigger);
        org.junit.Assert.assertNotNull(trigger.getKey());
        org.junit.Assert.assertNotNull(target);
        org.junit.Assert.assertNotNull(target.getKey());


        EntityName name = CommonFactory.createName("calc", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "1+1",EntityModelFactory.createTarget(target.getKey()),"","", "");

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);


        Entity en2 = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation2 = CalculationModelFactory.createCalculation(en2, EntityModelFactory.createTrigger(target.getKey()),
                true, "1+1", EntityModelFactory.createTarget(target2.getKey()), "", "", "");

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation2);

        Entity en3 = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation3 = CalculationModelFactory.createCalculation(en3,  EntityModelFactory.createTrigger(target2.getKey()),
                true, "1+1", EntityModelFactory.createTarget(trigger.getKey()), "", "", "");

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation3);

    }

    private Point addPoint(EntityName name) throws Exception {
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
                PointType.basic, 0, false, 0.0);
        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, Arrays.<Entity>asList(p)).get(0);
    }

    @Test(expected=Exception.class)
    public void testBadCalc1() throws Exception {


        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y = addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, (trigger.getKey()),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "X+y+z", EntityModelFactory.createTarget(target.getKey()), (trigger.getKey()), y.getKey(), z.getKey());

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);


    }

    @Test(expected=Exception.class)
    public void testBadCalcY() throws Exception {


        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, (trigger.getKey()),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "x+Y+z", EntityModelFactory.createTarget(target.getKey()), trigger.getKey(), y.getKey(), z.getKey());

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);


    }
    @Test(expected=Exception.class)
    public void testBadCalcZ() throws Exception {


        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, (trigger.getKey()),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "x+y+Z", EntityModelFactory.createTarget(target.getKey()), trigger.getKey(), y.getKey(), z.getKey());

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);


    }
    @Test(expected=Exception.class)
    public void testBadCalcMissingZ() throws Exception {


        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);



        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, (trigger.getKey()),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "x+y+z", EntityModelFactory.createTarget(target.getKey()), trigger.getKey(), y.getKey(),"");

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);


    }

    @Test(expected=Exception.class)
    public void testBadCalcMissingX() throws Exception {


        final EntityName targetName = CommonFactory.createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactory.createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactory.createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, (trigger.getKey()),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, EntityModelFactory.createTrigger(trigger.getKey()),
                true, "x+y+z", EntityModelFactory.createTarget(target.getKey()), "", y.getKey(),z.getKey());

        EntityServiceFactory.getInstance().addUpdateSingleEntity(calculation);


    }
}
