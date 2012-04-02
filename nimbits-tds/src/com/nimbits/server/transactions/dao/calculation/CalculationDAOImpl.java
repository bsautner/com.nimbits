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

package com.nimbits.server.transactions.dao.calculation;

import com.nimbits.PMF;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.calculation.CalculationTransactions;
import com.nimbits.server.orm.CalcEntity;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;

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



        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Calculation c = pm.getObjectById(CalcEntity.class, entity.getKey());
            return CalculationModelFactory.createCalculation(c);


        } finally {
            pm.close();
        }




    }

    @Override
    public List<Calculation> getCalculations(final Entity entity) {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalcEntity.class);
            q.setFilter("trigger == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Calculation> results = (List<Calculation>) q.execute(entity.getKey());
            return CalculationModelFactory.createCalculations(results);

        } finally {
            pm.close();
        }




    }

    @Override
    public void deleteCalculation(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalcEntity.class);
            q.setFilter("trigger == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Calculation> results = (List<Calculation>) q.execute(entity.getKey());
            pm.deletePersistentAll(results);

        } finally {
            pm.close();
        }
    }

    @Override
    public Calculation addUpdateCalculation(final Entity entity, final Calculation calculation) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<CalcEntity> results;


        try {

            if (calculation.getKey() != null ) {
                final CalcEntity result = pm.getObjectById(CalcEntity.class, calculation.getKey());
                if (result != null) {
                    final Transaction tx = pm.currentTransaction();
                    tx.begin();
                    result.setEnabled(calculation.getEnabled());
                    result.setFormula(calculation.getFormula());
                    result.setTarget(calculation.getTarget());
                    result.setX(calculation.getX());
                    result.setY(calculation.getY());
                    result.setZ(calculation.getZ());

                    tx.commit();
                    //retObj = EntityTransactionFactory.getInstance(user).getEntityByUUID(result.getKey());
                    pm.flush();
                    return CalculationModelFactory.createCalculation(result);
                }
                else {
                    return createCalc(entity, calculation, pm);
                }
            }

            else {

                return createCalc(entity, calculation, pm);

            }


        }
        finally {
            pm.close();
        }

    }

    private Calculation createCalc(Entity entity, Calculation calculation, PersistenceManager pm) {
        final CalcEntity s = new CalcEntity(entity, calculation);
        pm.makePersistent(s);
        return CalculationModelFactory.createCalculation(s);
    }
}

