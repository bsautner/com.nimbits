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

import com.nimbits.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.summary.*;

import javax.jdo.*;
import java.util.*;

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
    public Summary addOrUpdateSummary(final Summary summary) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            final SummaryEntity result = readSummaryEntity(pm, summary);
            if (result != null) {

                final Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setLastProcessed(new Date());
                result.setSummaryIntervalMs(summary.getSummaryIntervalMs());
                result.setSummaryType(summary.getSummaryType());
                tx.commit();
                pm.flush();
                return SummaryModelFactory.createSummary(result);
            }
            else {
                final SummaryEntity s = new SummaryEntity(summary);
                pm.makePersistent(s);
                return SummaryModelFactory.createSummary(s);
             }
        }
        finally {
            pm.close();
        }
    }

    @Override
    public Summary readSummary(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final SummaryEntity result = readSummaryEntity(pm, entity);
            return result != null ? SummaryModelFactory.createSummary(result) : null;
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
