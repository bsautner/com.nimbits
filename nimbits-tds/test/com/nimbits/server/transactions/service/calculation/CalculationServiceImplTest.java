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
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 7:14 PM
 */
public class CalculationServiceImplTest extends NimbitsServletTest {


    private static final double DELTA = 0.0001;

    @Test
    public void testCalcs() throws NimbitsException, InterruptedException {


        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);


        //final EntityName cName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.calculation);
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

        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z+" + r1, target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        final Entity ce = EntityServiceFactory.getInstance().addUpdateEntity(calculation);

        org.junit.Assert.assertNotNull(ce);

        final List<Entity> c =EntityServiceFactory.getInstance().getEntityByKey(user, ce.getKey(), EntityType.calculation);// CalculationServiceFactory.getInstance().getCalculation(ce);
        org.junit.Assert.assertNotNull(c);
        org.junit.Assert.assertFalse(c.isEmpty());

        EntityServiceFactory.getInstance().addUpdateEntity(trigger);
        //PointServiceFactory.getInstance().updatePoint(trigger);

        ValueServiceFactory.getInstance().recordValue(user,yName, ValueFactory.createValueModel(ry));

        ValueServiceFactory.getInstance().recordValue(user, zName, ValueFactory.createValueModel(rz));
        Thread.sleep(200);
        final Value vt = ValueServiceFactory.getInstance().recordValue(user, triggerName,  ValueFactory.createValueModel(r2));

        org.junit.Assert.assertEquals(vt.getDoubleValue(), r2, DELTA);

        final  List<Value> vy = ValueServiceFactory.getInstance().getCurrentValue(y);// ClientHelper.client().getCurrentRecordedValue(yName);
        org.junit.Assert.assertEquals(vy.get(0).getDoubleValue(), ry, DELTA);

        final  List<Value> vz = ValueServiceFactory.getInstance().getCurrentValue(z);
        org.junit.Assert.assertEquals(vz.get(0).getDoubleValue(), rz, DELTA);

        Thread.sleep(1000);
        CalculationServiceFactory.getInstance().processCalculations(user, trigger, vt);
        Thread.sleep(1000);
        final  List<Value> endResult = ValueServiceFactory.getInstance().getCurrentValue(target);
        org.junit.Assert.assertNotNull(endResult);
        org.junit.Assert.assertEquals(r1 + r2 + ry + rz, endResult.get(0).getDoubleValue(), DELTA);

    }

    @Test(expected=NimbitsException.class)
    public void testCalcLoop() throws NimbitsException, InterruptedException {

        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);

        //final EntityName cName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.calculation);
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


        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z", trigger.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        final Entity ce = EntityServiceFactory.getInstance().addUpdateEntity(calculation);

        org.junit.Assert.assertNotNull(ce);

        final List<Entity> c =EntityServiceFactory.getInstance().getEntityByKey(user, ce.getKey(), EntityType.calculation);// CalculationServiceFactory.getInstance().getCalculation(ce);
        org.junit.Assert.assertNotNull(c);
        org.junit.Assert.assertFalse(c.isEmpty());

        EntityServiceFactory.getInstance().addUpdateEntity(trigger);
        //PointServiceFactory.getInstance().updatePoint(trigger);

    }


    @Test(expected=NimbitsException.class)
    public void testCalcRecursion() throws NimbitsException, InterruptedException {

        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET", EntityType.point);
        final EntityName targetName2 = CommonFactoryLocator.getInstance().createName("TARGET2", EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER", EntityType.point);
        final EntityName triggerName2 = CommonFactoryLocator.getInstance().createName("TRIGGER2", EntityType.point);

        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);
        final Point target2 = addPoint(targetName2);
        final Point trigger2 = addPoint(triggerName2);

        org.junit.Assert.assertNotNull(trigger);
        org.junit.Assert.assertNotNull(trigger.getKey());
        org.junit.Assert.assertNotNull(target);
        org.junit.Assert.assertNotNull(target.getKey());


        EntityName name = CommonFactoryLocator.getInstance().createName("calc", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "1+1", target.getKey(),"","", "");

        EntityServiceFactory.getInstance().addUpdateEntity(calculation);


        Entity en2 = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation2 = CalculationModelFactory.createCalculation(en2, target.getKey(),
                true, "1+1", target2.getKey(), "", "", "");

        EntityServiceFactory.getInstance().addUpdateEntity(calculation2);

        Entity en3 = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, user.getKey(),
                user.getKey());

        final Calculation calculation3 = CalculationModelFactory.createCalculation(en3,  target2.getKey(),
                true, "1+1", trigger.getKey(), "", "", "");

        EntityServiceFactory.getInstance().addUpdateEntity(calculation3);

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
        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, p);
    }

    @Test(expected=NimbitsException.class)
    public void testBadCalc1() throws NimbitsException, InterruptedException {


        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y = addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "X+y+z", target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        EntityServiceFactory.getInstance().addUpdateEntity(calculation);


    }

    @Test(expected=NimbitsException.class)
    public void testBadCalcY() throws NimbitsException, InterruptedException {


        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+Y+z", target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        EntityServiceFactory.getInstance().addUpdateEntity(calculation);


    }
    @Test(expected=NimbitsException.class)
    public void testBadCalcZ() throws NimbitsException, InterruptedException {


        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+Z", target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        EntityServiceFactory.getInstance().addUpdateEntity(calculation);


    }
    @Test(expected=NimbitsException.class)
    public void testBadCalcMissingZ() throws NimbitsException, InterruptedException {


        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);



        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z", target.getKey(), trigger.getKey(), y.getKey(),"");

        EntityServiceFactory.getInstance().addUpdateEntity(calculation);


    }

    @Test(expected=NimbitsException.class)
    public void testBadCalcMissingX() throws NimbitsException, InterruptedException {


        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final Point trigger = addPoint(triggerName);
        final Point target = addPoint(targetName);

        final Point y =addPoint(yName);
        final Point z = addPoint(zName);


        EntityName name = CommonFactoryLocator.getInstance().createName("Calc 1", EntityType.calculation);
        Entity en = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                user.getKey());

        final Calculation calculation = CalculationModelFactory.createCalculation(en, trigger.getKey(),
                true, "x+y+z", target.getKey(), "", y.getKey(),z.getKey());

        EntityServiceFactory.getInstance().addUpdateEntity(calculation);


    }
}
