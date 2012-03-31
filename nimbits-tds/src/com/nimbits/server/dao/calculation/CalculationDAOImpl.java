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

package com.nimbits.server.dao.calculation;

import com.nimbits.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.calculation.*;
import com.nimbits.server.orm.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:24 PM
 */
@SuppressWarnings({"unchecked", "unused"})

public class CalculationDAOImpl implements CalculationTransactions {

    public CalculationDAOImpl(final User user) {

    }

    @Override
    public Calculation getCalculation(final Entity entity) {


        Calculation retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalculationEntity.class, "uuid == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Calculation> results = (List<Calculation>) q.execute(entity.getEntity());
            if (results.size() > 0) {
                retObj = CalculationModelFactory.createCalculation(results.get(0));
            }
            return retObj;
        } finally {
            pm.close();
        }




    }

    @Override
    public List<Calculation> getCalculations(final Entity entity) {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalculationEntity.class, "trigger == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Calculation> results = (List<Calculation>) q.execute(entity.getEntity());
            return CalculationModelFactory.createCalculations(results);

        } finally {
            pm.close();
        }




    }

    @Override
    public void deleteCalculation(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalculationEntity.class, "trigger == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Calculation> results = (List<Calculation>) q.execute(entity.getEntity());
            pm.deletePersistentAll(results);

        } finally {
            pm.close();
        }
    }

    @Override
    public Calculation addUpdateCalculation(final Calculation calculation) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<CalculationEntity> results;


        try {

            final Query q = pm.newQuery(CalculationEntity.class, "uuid==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<CalculationEntity>) q.execute(calculation.getUUID());
            if (results.size() > 0) {
                final CalculationEntity result = results.get(0);
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setEnabled(calculation.getEnabled());
                result.setFormula(calculation.getFormula());
                result.setTarget(calculation.getTarget());
                result.setX(calculation.getX());
                result.setY(calculation.getY());
                result.setZ(calculation.getZ());

                tx.commit();
                //retObj = EntityTransactionFactory.getInstance(user).getEntityByUUID(result.getUuid());
                pm.flush();
                return CalculationModelFactory.createCalculation(result);

            }
            else {
                final CalculationEntity s = new CalculationEntity(calculation);
                pm.makePersistent(s);
                return CalculationModelFactory.createCalculation(s);

            }


        }
        finally {
            pm.close();
        }

    }
}

