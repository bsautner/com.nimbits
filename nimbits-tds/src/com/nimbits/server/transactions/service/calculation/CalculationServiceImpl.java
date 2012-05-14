/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.trigger.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.calculation.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.transactions.service.value.*;

import java.util.*;
import java.util.logging.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:21 PM
 */
public class CalculationServiceImpl extends RemoteServiceServlet implements CalculationService {
    final Logger log = Logger.getLogger(CalculationServiceImpl.class.getName());


    @Override
    public void processCalculations(final User u, final Entity point, final Value value) throws NimbitsException {

        final List<Entity> calculations = EntityServiceFactory.getInstance().getEntityByTrigger(u, point, EntityType.calculation);
        for (final Entity entity : calculations) {
                Calculation c = (Calculation)entity;
                try {

                    final List<Entity> target =   EntityServiceFactory.getInstance().getEntityByKey(u, c.getTarget(),EntityType.point);
                    if (target.isEmpty()) {
                        log.severe("Point target was null " + c.getTarget());
                        log.severe(c.getFormula());
                        log.severe("trigger: " + c.getTrigger());
                        disableCalc(u, c);
                    }
                    else {
                        log.info("Solving calc" + c.getFormula());
                        final Value result = solveEquation(u, c);
                        log.info("result" + result);
                        ValueServiceFactory.getInstance().recordValue(u, target.get(0), result);
                    }
                } catch (NimbitsException e1) {
                    LogHelper.logException(this.getClass(), e1);
                    disableCalc(u, c);
                }

        }
    }

    private static void disableCalc(User u, Trigger c) throws NimbitsException {
        c.setEnabled(false);
        EntityServiceFactory.getInstance().addUpdateEntity(u, c);
    }

    @Override
    public  Value solveEquation(final User user, final Calculation calculation) throws NimbitsException {

        final MathEvaluator m = new MathEvaluatorImpl(calculation.getFormula());
        log.info(calculation.getFormula());

        if (!Utils.isEmptyString(calculation.getX()) && calculation.getFormula().contains("x")) {
            //  Point p = PointServiceFactory.getInstance().getPointByKey(calculation.getX());
            final Entity p =  EntityServiceFactory.getInstance().getEntityByKey(user, calculation.getX(), EntityType.point).get(0);

            if (p != null) {
                log.info("calc has an x car and i found " + p.getName());
                final Value val = ValueServiceFactory.getInstance().getCurrentValue(p);
                final double d = val == null ? 0.0 : val.getDoubleValue();

                m.addVariable("x", d);
            }
            else {
                log.severe("calc has an x car and x not found");
            }
        }
        if (!Utils.isEmptyString(calculation.getY()) && calculation.getFormula().contains("y")) {
            final Entity p =  EntityServiceFactory.getInstance().getEntityByKey(user, calculation.getY(), EntityType.point).get(0);

            // Point p = PointServiceFactory.getInstance().getPointByKey(calculation.getY());
            if (p != null) {
                final Value val = ValueServiceFactory.getInstance().getCurrentValue(p);
                final double d = val == null ? 0.0 : val.getDoubleValue();
                m.addVariable("y", d);
            }

        }
        if (!Utils.isEmptyString(calculation.getZ()) && calculation.getFormula().contains("z")) {
            final Entity p =  EntityServiceFactory.getInstance().getEntityByKey(user, calculation.getZ(), EntityType.point).get(0);

            //  Point p = PointServiceFactory.getInstance().getPointByKey(calculation.getZ());
            if (p != null) {
                final Value val = ValueServiceFactory.getInstance().getCurrentValue(p);
                final double d = val == null ? 0.0 : val.getDoubleValue();
                m.addVariable("z", d);
            }
        }

        final Double retVal = m.getValue();


        if (retVal == null) {

            throw new NimbitsException("Formula returned a null value: " + calculation.getFormula());


        }


        return ValueFactory.createValueModel(retVal, "CV");
    }
}
