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

package com.nimbits.server.transactions.dao.point;

import com.nimbits.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.point.*;

import javax.jdo.*;
import java.util.*;
@SuppressWarnings(Const.WARNING_UNCHECKED)
public class PointDaoImpl implements PointTransactions {
   // private final Logger log = Logger.getLogger(PointDaoImpl.class.getName());

    public PointDaoImpl() {

    }


    @Override
    public List<Point> getIdlePoints() throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Point> retObj;
        try {

            final Query q = pm
                    .newQuery(PointEntity.class);
            q.setFilter("idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            final Collection<Point> points = (Collection<Point>) q.execute(true, false);
            retObj =PointModelFactory.createPointModels(points);// createPointModels( points);
        } finally {
            pm.close();
        }


        return retObj;

    }




}


