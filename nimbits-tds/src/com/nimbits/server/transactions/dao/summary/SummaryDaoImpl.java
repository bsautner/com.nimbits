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

package com.nimbits.server.transactions.dao.summary;

import com.nimbits.PMF;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.summary.SummaryModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.server.orm.SummaryEntity;
import com.nimbits.server.summary.SummaryTransactions;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 11:13 AM
 */

@SuppressWarnings({"unchecked", "unused"})
public class SummaryDaoImpl implements SummaryTransactions {

    public SummaryDaoImpl(final User u) {

    }

    private static SummaryEntity readSummaryEntity(final PersistenceManager pm, final Entity entity) {

         try {
             return pm.getObjectById(SummaryEntity.class, entity.getKey());
         }
         catch (JDOObjectNotFoundException ex) {
             return null;
         }

    }


    @Override
    public void addOrUpdateSummary(final Entity entity,final Summary summary)  {

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            final SummaryEntity result = readSummaryEntity(pm, entity);
            if (result != null) {

                final Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setLastProcessed(new Date());
                result.setSummaryIntervalMs(summary.getSummaryIntervalMs());
                result.setSummaryType(summary.getSummaryType());
                tx.commit();
                pm.flush();
            }
            else {
                final SummaryEntity s = new SummaryEntity(entity, summary);
                pm.makePersistent(s);
             }
        }
        finally {
            pm.close();
        }
    }

    @Override
    public Summary readSummary(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final SummaryEntity result = readSummaryEntity(pm, entity);
            if (result != null) {

                return SummaryModelFactory.createSummary(result);
            }
            else {
                return null;
            }
        }
        finally {
            pm.close();
        }
    }

    @Override
    public void updateLastProcessed(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final SummaryEntity result = readSummaryEntity(pm, entity);
            if (result != null) {

                final Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setLastProcessed(new Date());
                tx.commit();
                pm.flush();
            }

        }
        finally {
            pm.close();
        }
    }

    @Override
    public void deleteSummary(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {

            final SummaryEntity result = readSummaryEntity(pm, entity);
            if (result != null) {
            pm.deletePersistentAll(result);
            }
        }
        finally {
            pm.close();
        }
    }
}
