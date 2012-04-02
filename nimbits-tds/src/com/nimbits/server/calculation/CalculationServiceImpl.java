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

package com.nimbits.server.calculation;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.client.service.calculation.CalculationService;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;

import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:21 PM
 */
public class CalculationServiceImpl extends RemoteServiceServlet implements CalculationService {

    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }



    @Override
    public Calculation getCalculation(Entity entity) {
        return CalculationServiceFactory.getDaoInstance(getUser()).getCalculation(entity);
    }

    @Override
    public Entity addUpdateCalculation(User u, Entity entity, EntityName name, Calculation calculation) throws NimbitsException {
        Entity retObj = null;

        if (entity == null) {


            final Entity e = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe,
                    calculation.getTrigger(), u.getKey());
            retObj = EntityServiceFactory.getInstance().addUpdateEntity(u, e);
            final Calculation c = CalculationModelFactory.createCalculation(calculation.getTrigger(), e.getKey(), calculation.getEnabled(),
                    calculation.getFormula(), calculation.getTarget(), calculation.getX(),
                    calculation.getY(), calculation.getZ());

            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(retObj, c);


        }
        else if (entity.getEntityType().equals(EntityType.point) && Utils.isEmptyString(calculation.getKey())) {

            Entity e = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe,
                    entity.getKey(), u.getKey());
            retObj = EntityServiceFactory.getInstance().addUpdateEntity(e);
            Calculation c = CalculationModelFactory.createCalculation(entity.getKey(), e.getKey(), calculation.getEnabled(),
                    calculation.getFormula(), calculation.getTarget(), calculation.getX(),
                    calculation.getY(), calculation.getZ());

            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(retObj, c);


        }
        else if (entity.getEntityType().equals(EntityType.calculation)) {
            entity.setName(name);
            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(entity, calculation);
            return EntityServiceFactory.getInstance().addUpdateEntity(entity);


        }


        return  retObj;
    }

    @Override
    public void deleteCalculation(final User u, final Entity entity) {
      CalculationServiceFactory.getDaoInstance(u).deleteCalculation(entity);
    }

    @Override
    public Entity addUpdateCalculation(Entity entity, EntityName name, Calculation calculation) throws NimbitsException {
         User u = getUser();
         return addUpdateCalculation(u, entity, name, calculation);
    }

    //Section - Calls from RPC Client


    @Override
    public List<Calculation> getCalculations(Entity entity) {
        return CalculationServiceFactory.getDaoInstance(getUser()).getCalculations(entity);
    }

    @Override
    public void processCalculations(final User u, final Point point, final Value value) throws NimbitsException {

        final Entity e = EntityServiceFactory.getInstance().getEntityByKey(point.getKey());

        final List<Calculation> calculations = getCalculations(e);
        Point target;
        Value result;
        for (final Calculation c : calculations) {
            if (c.getEnabled()) {
                target = PointServiceFactory.getInstance().getPointByKey(c.getTarget());
                try {
                    result= solveEquation(c);
                    RecordedValueServiceFactory.getInstance().recordValue(u, target, result, true);
                } catch (NimbitsException e1) {
                    c.setEnabled(false);
                    CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(null, c);

                }


            }
        }
    }
    @Override
    public  Value solveEquation(final Calculation calculation) throws NimbitsException {

        Double retVal;

        final MathEvaluator m = new MathEvaluator(calculation.getFormula());


        if (!(Utils.isEmptyString(calculation.getX())) && calculation.getFormula().contains("x")) {
            Point p = PointServiceFactory.getInstance().getPointByKey(calculation.getX());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getDoubleValue();

                m.addVariable("x", d);
            }
        }
        if (!(Utils.isEmptyString(calculation.getY())) && calculation.getFormula().contains("y")) {

            Point p = PointServiceFactory.getInstance().getPointByKey(calculation.getY());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getDoubleValue();
                m.addVariable("y", d);
            }

        }
        if (!(Utils.isEmptyString(calculation.getZ())) && calculation.getFormula().contains("z")) {
            Point p = PointServiceFactory.getInstance().getPointByKey(calculation.getZ());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getDoubleValue();
                m.addVariable("z", d);
            }
        }

        retVal = m.getValue();


        if (retVal == null) {

            throw new NimbitsException("Formula returned a null value: " + calculation.getFormula());


        }


        return ValueModelFactory.createValueModel(retVal, "CV");
    }
}
