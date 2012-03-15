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

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.calculation.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.user.*;

import java.util.*;

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

            String uuid = UUID.randomUUID().toString();
            Entity e = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, uuid,
                    calculation.getTrigger(), u.getUuid());
            retObj = EntityServiceFactory.getInstance().addUpdateEntity(u, e);
            Calculation c = CalculationModelFactory.createCalculation(calculation.getTrigger(), uuid, calculation.getEnabled(),
                    calculation.getFormula(), calculation.getTarget(), calculation.getX(),
                    calculation.getY(), calculation.getZ());

            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(c);


        }
        else if (entity.getEntityType().equals(EntityType.point) && Utils.isEmptyString(calculation.getUUID())) {
            String uuid = UUID.randomUUID().toString();
            Entity e = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, uuid,
                    entity.getEntity(), u.getUuid());
            retObj = EntityServiceFactory.getInstance().addUpdateEntity(e);
            Calculation c = CalculationModelFactory.createCalculation(entity.getEntity(), uuid, calculation.getEnabled(),
                    calculation.getFormula(), calculation.getTarget(), calculation.getX(),
                    calculation.getY(), calculation.getZ());

            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(c);


        }
        else if (entity.getEntityType().equals(EntityType.calculation)) {
            entity.setName(name);
            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(calculation);
            return EntityServiceFactory.getInstance().addUpdateEntity(entity);


        }


        return  retObj;
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
    public void processCalculations(User u, Point point, Value value) {

        Entity e = EntityServiceFactory.getInstance().getEntityByUUID(point.getUUID());

        List<Calculation> calculations = getCalculations(e);
        for (Calculation c : calculations) {
            if (c.getEnabled()) {
               Point target = PointServiceFactory.getInstance().getPointByUUID(c.getTarget());
                try {
                    Value result = solveEquation(c);
                    RecordedValueServiceFactory.getInstance().recordValue(u, target, result, true);
                } catch (NimbitsException e1) {
                    c.setEnabled(false);
                    CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(c);

                }


            }
        }
    }
    @Override
    public  Value solveEquation(final Calculation calculation) throws NimbitsException {

        Double retVal;

        final MathEvaluator m = new MathEvaluator(calculation.getFormula());


        if (!(Utils.isEmptyString(calculation.getX())) && calculation.getFormula().contains("x")) {
            Point p = PointServiceFactory.getInstance().getPointByUUID(calculation.getX());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getNumberValue();

                m.addVariable("x", d);
            }
        }
        if (!(Utils.isEmptyString(calculation.getY())) && calculation.getFormula().contains("y")) {

            Point p = PointServiceFactory.getInstance().getPointByUUID(calculation.getY());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getNumberValue();
                m.addVariable("y", d);
            }

        }
        if (!(Utils.isEmptyString(calculation.getZ())) && calculation.getFormula().contains("z")) {
            Point p = PointServiceFactory.getInstance().getPointByUUID(calculation.getZ());
            if (p != null) {
                Value val = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                double d = val == null ? 0.0 : val.getNumberValue();
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
