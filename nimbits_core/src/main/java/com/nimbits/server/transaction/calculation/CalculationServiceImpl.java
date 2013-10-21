/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transaction.calculation;

import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/2/13
 * Time: 7:19 PM
 */
public class CalculationServiceImpl implements CalculationService {

    private final EntityService entityService;
    private final ValueService valueService;

    public CalculationServiceImpl(NimbitsEngine engine, TaskService taskService) {
        NimbitsEngine engine1 = engine;
        entityService = EntityServiceFactory.getInstance(engine);
        valueService = ValueServiceFactory.getInstance(engine, taskService);
    }

    @Override
    public void processCalculations(final User u, final Entity point)   {

        final List<Entity> calculations = entityService.getEntityByTrigger(u, point, EntityType.calculation);
        for (final Entity entity : calculations) {
            Calculation c = (Calculation)entity;


            final List<Entity> target =   entityService.getEntityByKey(u, c.getTarget(), EntityType.point);
            if (! target.isEmpty()) {

                final List<Value> result = solveEquation(u, c);
                if (! result.isEmpty())  {

                    valueService.recordValue(u, target.get(0), result.get(0));
                }
            }


        }
    }

    @Override
    public List<Value> solveEquation(final User user, final Calculation calculation)  {

        final MathEvaluator m = new MathEvaluatorImpl(calculation.getFormula());


        if (!Utils.isEmptyString(calculation.getX()) && calculation.getFormula().contains("x")) {

            final Entity p =  entityService.getEntityByKey(user, calculation.getX(), EntityType.point).get(0);

            if (p != null) {

                final List<Value> val = valueService.getCurrentValue(p);

                final double d = val.isEmpty() ? 0.0 : val.get(0).getDoubleValue();

                m.addVariable("x", d);
            }

        }
        if (!Utils.isEmptyString(calculation.getY()) && calculation.getFormula().contains("y")) {
            final Entity p = entityService.getEntityByKey(user, calculation.getY(), EntityType.point).get(0);

            if (p != null) {
                final List<Value> val = valueService.getCurrentValue(p);
                final double d = val.isEmpty() ? 0.0 : val.get(0).getDoubleValue();
                m.addVariable("y", d);
            }

        }
        if (!Utils.isEmptyString(calculation.getZ()) && calculation.getFormula().contains("z")) {
            final Entity p = entityService.getEntityByKey(user, calculation.getZ(), EntityType.point).get(0);

            if (p != null) {
                final List<Value> val = valueService.getCurrentValue(p);
                final double d = val.isEmpty() ? 0.0 : val.get(0).getDoubleValue();
                m.addVariable("z", d);
            }
        }

        final Double retVal = m.getValue();


        if (retVal == null) {
            return Collections.emptyList();
        }
        else {
            Value v =ValueFactory.createValueModel(retVal);
            return Arrays.asList(v);
        }

    }
}
