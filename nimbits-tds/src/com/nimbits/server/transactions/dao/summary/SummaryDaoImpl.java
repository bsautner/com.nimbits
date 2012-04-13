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

    public SummaryDaoImpl() {

    }


    @Override
    public List<Summary> readSummariesToEntity(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(SummaryEntity.class);
            q.setFilter("entity == e");
            q.declareParameters("String e");
            List<Summary> result = (List<Summary>) q.execute(entity.getKey());
            return SummaryModelFactory.createSummaries(result);

        }
        finally {
            pm.close();
        }
    }

}
