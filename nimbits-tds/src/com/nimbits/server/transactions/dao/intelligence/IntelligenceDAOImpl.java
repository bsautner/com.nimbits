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

package com.nimbits.server.transactions.dao.intelligence;

import com.nimbits.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.server.intelligence.*;
import com.nimbits.server.orm.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/21/12
 * Time: 11:50 AM
 */
@SuppressWarnings("unchecked")
public class IntelligenceDAOImpl implements IntelligenceTransactions {


    @Override
    public Intelligence getIntelligence(final Entity entity) throws NimbitsException {
        Intelligence retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(IntelligenceEntity.class);
            q.setFilter("uuid == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final List<Intelligence> results = (List<Intelligence>) q.execute(entity.getKey());
            if (!results.isEmpty()) {
                retObj = IntelligenceFactory.createIntelligence(results.get(0));
            }
            return retObj;
        } finally {
            pm.close();
        }
    }



    private static Intelligence createIntelligence(Intelligence update, PersistenceManager pm) throws NimbitsException {
        final IntelligenceEntity s = new IntelligenceEntity(update);

        pm.makePersistent(s);
        return IntelligenceFactory.createIntelligence(s);
    }

    @Override
    public List<Intelligence> getIntelligences(final Entity point) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(IntelligenceEntity.class);
            q.setFilter("target == k && enabled == e");
            q.declareParameters("String k, Boolean e");
            q.setRange(0,1);
            final Collection<Intelligence> results = (Collection<Intelligence>) q.execute(point.getKey(), true);
            return  IntelligenceFactory.createIntelligences(results);
        } finally {
            pm.close();
        }
    }

    @Override
    public void deleteIntelligence(final Entity entity) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(IntelligenceEntity.class, "uuid == k");
            q.declareParameters("String k");
            q.setRange(0,1);
            final Collection<Intelligence> results = (List<Intelligence>) q.execute(entity.getKey());
            pm.deletePersistentAll(results);

        } finally {
            pm.close();
        }
    }
}
