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
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.point.*;
import com.nimbits.server.task.*;

import javax.jdo.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;
@SuppressWarnings(Const.WARNING_UNCHECKED)
public class PointDaoImpl implements PointTransactions {
    private final Logger log = Logger.getLogger(PointDaoImpl.class.getName());

    public PointDaoImpl(final User u) {
        User u1 = u;
    }

    public static List<Point> createPointModels(final Collection<Point> points) throws NimbitsException {
        final List<Point> retObj = new ArrayList<Point>(points.size());

        for (final Point p : points) {
            retObj.add(PointModelFactory.createPointModel(p));
        }

        return retObj;


    }


    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getAllPoints() throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(PointEntity.class);
            final Collection<Point> result = (Collection<Point>) q.execute();
            return createPointModels( result);

        } finally {
            pm.close();
        }
    }


    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getPoints(final List<Entity> entities) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Collection<Point> points = new ArrayList<Point>(entities.size());
            for (final Entity e : entities) {
                if (e.getEntityType().equals(EntityType.point)) {
                    points.add( pm.getObjectById(PointEntity.class, e.getKey()));
                }

            }
            return createPointModels(points);

        } finally {
            pm.close();
        }
    }

    @Override
    public Point deletePoint(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {


            final Point p = pm.getObjectById(PointEntity.class, entity.getKey());

            final Point retObj =PointModelFactory.createPointModel(p);

            pm.deletePersistent(p);
            return retObj;




        } finally {
            pm.close();
        }

    }



    @Override
    public Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Transaction tx = pm.currentTransaction();
        final Point retObj;

        try {
            tx.begin();

            final PointEntity p = pm.getObjectById(PointEntity.class, point.getKey());


            if (p.getExpire() > 0) {
                TaskFactory.getInstance().startDeleteDataTask(
                        p,
                        true, p.getExpire());
            }
            retObj = PointModelFactory.createPointModel(p);

            tx.commit();
            return retObj;
        }catch (Exception ex) {
            log.severe(ex.getMessage());
            throw new NimbitsException(ex.getMessage());
        } finally {
            pm.close();
        }

    }


    /* (non-Javadoc)
    * @see com.nimbits.server.point.PointTransactions#getAllPoints()
    */
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getAllPoints(final int start, final int end) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Point> retObj;
        try {

            final Query q = pm.newQuery(PointEntity.class);
            q.setRange(start, end);
            final Collection<Point> points = (Collection<Point>) q.execute();

            retObj = createPointModels(  points);
            return retObj;
        } finally {
            pm.close();
        }


    }


    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getIdlePoints() throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Point> retObj;
        try {
            final List<Point> points;

            final Query q = pm
                    .newQuery(PointEntity.class);
            q.setFilter("idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            points = (List<Point>) q.execute(true, false);
            retObj = createPointModels( points);
        } finally {
            pm.close();
        }


        return retObj;

    }




}


