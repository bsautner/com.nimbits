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

package com.nimbits.server.dao.intelligence;

import com.nimbits.PMF;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.intelligence.IntelligenceTransactions;
import com.nimbits.server.orm.DataPointIntelligenceEntity;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/21/12
 * Time: 11:50 AM
 */
@SuppressWarnings("unchecked")
public class IntelligenceDAOImpl implements IntelligenceTransactions {


    @Override
    public Intelligence getIntelligence(final Entity entity) {
        Intelligence retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPointIntelligenceEntity.class);
            q.setFilter("uuid == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Intelligence> results = (List<Intelligence>) q.execute(entity.getEntity());
            if (results.size() > 0) {
                retObj = IntelligenceFactory.createIntelligence(results.get(0));
            }
            return retObj;
        } finally {
            pm.close();
        }
    }

    @Override
    public Intelligence addUpdateIntelligence(final Intelligence update) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Intelligence> results;


        try {

            final Query q = pm.newQuery(DataPointIntelligenceEntity.class);
            q.setFilter("uuid==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<Intelligence>) q.execute(update.getUUID());
            if (results.size() > 0) {
                final Intelligence result = results.get(0);
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setEnabled(update.getEnabled());
                result.setInput(update.getInput());
                result.setTarget(update.getTarget());
                result.setNodeId(update.getNodeId());
                result.setResultsInPlainText(update.getResultsInPlainText());
                result.setResultTarget(update.getResultTarget());
                result.setTrigger(update.getTrigger());

                tx.commit();
                pm.flush();
                return IntelligenceFactory.createIntelligence(result);

            }
            else {
                final DataPointIntelligenceEntity s = new DataPointIntelligenceEntity(update);

                pm.makePersistent(s);
                return IntelligenceFactory.createIntelligence(s);

            }


        }
        finally {
            pm.close();
        }
    }

    @Override
    public List<Intelligence> getIntelligence(final Point point) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPointIntelligenceEntity.class);
            q.setFilter("target == k && enabled == e");
            q.declareParameters("String k, Boolean e");
            q.setRange(0,1);
            final List<Intelligence> results = (List<Intelligence>) q.execute(point.getUUID(), true);
            return  IntelligenceFactory.createIntelligences(results);

        } finally {
            pm.close();
        }
    }

    @Override
    public void deleteIntelligence(final Entity entity) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPointIntelligenceEntity.class, "uuid == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Intelligence> results = (List<Intelligence>) q.execute(entity.getEntity());
            pm.deletePersistentAll(results);

        } finally {
            pm.close();
        }
    }
}
