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

import com.nimbits.PMF;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.point.PointTransactions;
import com.nimbits.server.task.TaskFactory;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
@SuppressWarnings(Const.WARNING_UNCHECKED)
public class PointDaoImpl implements PointTransactions {
    private final Logger log = Logger.getLogger(PointDaoImpl.class.getName());
    private final User u;

    public PointDaoImpl(final User u) {
        this.u = u;
    }





    @Override
    public Point updatePoint(final Point update) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj = null;
        try {
            final PointEntity original = pm.getObjectById(PointEntity.class, update.getKey());

            if (original != null) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                original.setHighAlarm(update.getHighAlarm());
                original.setLowAlarm(update.getLowAlarm());
                original.setUnit(update.getUnit());
                original.setExpire(update.getExpire());
                original.setIdleAlarmOn(update.isIdleAlarmOn());
                original.setIdleAlarmSent(update.getIdleAlarmSent());
                original.setIdleSeconds(update.getIdleSeconds());
                original.setFilterType(update.getFilterType());
                original.setFilterValue(update.getFilterValue());
                tx.commit();
                retObj = PointModelFactory.createPointModel(original);

            }
            return retObj;
        }

        finally {
            pm.close();

        }



    }

    /* (non-Javadoc)
      * @see com.nimbits.server.point.PointTransactions#getPointByUUID(java.lang.String)
      */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public Point getPointByKey(final String entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {

            final Point p =   pm.getObjectById(PointEntity.class, entity);
            return PointModelFactory.createPointModel(p);
        }
        catch (JDOObjectNotFoundException ex) {
            return null;

        } finally {
            pm.close();
        }

    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getAllPoints() {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(PointEntity.class);
            final List<Point> result = (List<Point>) q.execute();
            return PointModelFactory.createPointModels(result);

        } finally {
            pm.close();
        }
    }

    @Override
    public Point addPoint(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            final PointEntity jdoPoint = new PointEntity(entity);
            jdoPoint.setFilterValue(Const.DEFAULT_POINT_COMPRESSION);
            jdoPoint.setFilterType(FilterType.fixedHysteresis);
            jdoPoint.setExpire(Const.DEFAULT_DATA_EXPIRE_DAYS);
            pm.makePersistent(jdoPoint);

            return PointModelFactory.createPointModel(jdoPoint);
        } finally {
            pm.close();
        }
    }

    @Override
    public Point addPoint(final Entity entity, final Point point) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final PointEntity jdoPoint = new PointEntity(entity, point);

            pm.makePersistent(jdoPoint);

            return PointModelFactory.createPointModel(jdoPoint);
        } finally {
            pm.close();
        }
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getPoints(final List<Entity> entities) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();


        // final Query q1 = pm.newQuery(PointEntity.class, ":p.contains(uuid)");

        try {
            List<Point> points = new ArrayList<Point>(entities.size());
            for (final Entity e : entities) {
                if (e.getEntityType().equals(EntityType.point)) {
                    points.add(pm.getObjectById(PointEntity.class, e.getKey()));
                }

            }
            return PointModelFactory.createPointModels(points);

        } finally {
            pm.close();
        }
    }

    @Override
    public Point deletePoint(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        final List<PointEntity> points;
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
    public List<Point> getAllPoints(final int start, final int end) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Point> retObj;
        try {

            final Query q = pm.newQuery(PointEntity.class);
            q.setRange(start, end);
            final List<Point> points = (List<Point>) q.execute();

            retObj = PointModelFactory.createPointModels(points);
            return retObj;
        } finally {
            pm.close();
        }


    }


    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getIdlePoints() {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Point> retObj;
        try {
            final List<Point> points;

            final Query q = pm
                    .newQuery(PointEntity.class, "idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            points = (List<Point>) q.execute(true, false);
            retObj = PointModelFactory.createPointModels(points);
        } finally {
            pm.close();
        }


        return retObj;

    }




}


