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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.dao.datapoint;

import com.nimbits.PMF;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.datapoints.PointTransactions;
import com.nimbits.server.orm.DataPoint;
import com.nimbits.server.point.*;
import com.nimbits.server.pointcategory.CategoryServiceFactory;
import com.nimbits.server.task.TaskFactoryLocator;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DataPointDAOImpl implements PointTransactions {
    private final Logger log = Logger.getLogger(DataPointDAOImpl.class.getName());
    private final User u;

    public DataPointDAOImpl(final User u) {
        this.u = u;
    }

    /* (non-Javadoc)
    * @see com.nimbits.client.service.datapoints.PointTransactions#getPoints(com.nimbits.client.model.user.NimbitsUser)
    */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<Point> getPoints() {

        List<Point> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPoint.class, "userFK == k");
            q.declareParameters("Long k");
            final long userFK = u.getId();
            final List<Point> points = (List<Point>) q.execute(userFK);
            retObj = PointModelFactory.createPointModels(points);
        } finally {
            pm.close();
        }

        return retObj;
    }


    @Override
    public Point getPointByID(final long id) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj = null;
        try {
            final DataPoint p = pm.getObjectById(DataPoint.class, id);
            if (p != null) {
                retObj = PointModelFactory.createPointModel(p);
            }

        } catch (JDOObjectNotFoundException ex) {
            log.info("Point not found");

        } finally {
            pm.close();
        }

        return retObj;
    }


    @Override
    public Point updatePoint(final Point update) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj = null;
        try {
            DataPoint original = pm.getObjectById(DataPoint.class, update.getId());

            if (original != null) {
                Transaction tx = pm.currentTransaction();
                tx.begin();
                original.setName(update.getName());

                original.setHighAlarm(update.getHighAlarm());
                original.setAlarmDelay(update.getAlarmDelay());
                original.setLowAlarm(update.getLowAlarm());
                original.setLowAlarmOn(update.isLowAlarmOn());
                original.setHighAlarmOn(update.isHighAlarmOn());
                original.setCompression(update.getCompression());
                original.setUnit(update.getUnit());
                original.setDescription(update.getDescription());
                original.setExpire(update.getExpire());
                original.setPublic(update.isPublic());
                original.setTag(update.getTag());
                original.setCatID(update.getCatID());

                original.setUserFK(update.getUserFK());
                original.setSystemPoint(update.isSystemPoint());
                original.setPostToFacebook(update.isPostToFacebook());
                original.setAlarmToFacebook(update.getAlarmToFacebook());
                original.setSendIM(update.getSendIM());
                original.setSendAlarmIM(update.getSendAlarmIM());
                original.setSendTweet(update.getSendTweet());
                original.setSendAlarmTweet(update.getSendAlarmTweet());
                original.setHost(update.getHost());
                original.setLastChecked(update.getLastChecked());
                original.setTargetValue(update.getTargetValue());

                original.setIdleAlarmOn(update.isIdleAlarmOn());
                original.setIdleAlarmSent(update.getIdleAlarmSent());
                original.setIdleSeconds(update.getIdleSeconds());

                original.setIgnoreIncomingCompressedValues(update.getIgnoreIncomingCompressedValues());

                original.setAlarmToEmail(update.isAlarmToEmail());

                original.setSendAlertsAsJson(update.getSendAlertsAsJson());
                if (update.getIntelligence() != null) {
                    if (original.getIntelligence() == null) {
                        //  intelligenceEntity.setPoint(p);
                        original.setIntelligence(update.getIntelligence());
                    } else {
                        original.updateIntelligence(update.getIntelligence());
                    }
                }

                if (update.getCalculation() != null) {
                    if (original.getCalculation() == null) {
                        //  intelligenceEntity.setPoint(p);
                        original.setCalculation(update.getCalculation());
                    } else {
                        original.updateCalculation(update.getCalculation());
                    }
                }


                tx.commit();
                retObj = PointModelFactory.createPointModel(original);

            }
            return retObj;
        }

        finally {
            pm.close();

        }



    }

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public Point getPointByName(final PointName name) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        Point retObj = null;
        try {

            final Query q = pm.newQuery(DataPoint.class, "userFK==u && name==p");
            q.declareParameters("Long u, String p");
            q.setRange(0, 1);
            final List<DataPoint> points = (List<DataPoint>) q.execute(u.getId(), name.getValue());
            if (points.size() > 0) {
                DataPoint result = points.get(0);
                retObj = PointModelFactory.createPointModel(result);
            }

        } catch (Exception e) {
            retObj = null;
        } finally {
            pm.close();
        }

        return retObj;
    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#getPointByUUID(java.lang.String)
      */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public Point getPointByUUID(final String uuid) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj;

        List<DataPoint> points;
        try {
            Query q = pm.newQuery(DataPoint.class, "uuid == k");
            q.declareParameters("String k");
            q.setRange(0, 1);
            points = (List<DataPoint>) q.execute(uuid);

            if (points.size() > 0) {
                DataPoint result = points.get(0);
                result.setReadOnly(true);
                retObj = PointModelFactory.createPointModel(result);
            } else {
                retObj = null;
            }
        } finally {
            pm.close();
        }

        return retObj;
    }

    @Override
    public List<Point> getAllPoints() {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPoint.class);
            List<Point> result = (List<Point>) q.execute();
            return PointModelFactory.createPointModels(result);

        } finally {
            pm.close();
        }
    }


    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#deletePoint(com.nimbits.client.model.DataPoint)
      */
    @Override
    public void deletePoint(final Point p) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(DataPoint.class);
            q.setFilter("id==k");
            q.declareParameters("long k");
            q.deletePersistentAll(p.getId());
            TaskFactoryLocator.getInstance().startDeleteDataTask(p.getId(), false, 0, p.getName());
        } finally {
            pm.close();
        }
    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#movePoint(com.nimbits.client.model.user.NimbitsUser, java.lang.String, java.lang.String)
      */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public Point movePoint(final Point point, final CategoryName categoryName) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        Category c = CategoryServiceFactory.getInstance().getCategory(u, categoryName);

        if (!(c == null)) {
            Transaction tx;

            long userFK = u.getId();
            try {

                tx = pm.currentTransaction();
                tx.begin();
                Query q1 = pm.newQuery(DataPoint.class, "uuid==p");
                q1.declareParameters("String p");
                q1.setRange(0, 1);
                List<DataPoint> points = (List<DataPoint>) q1.execute(point.getUUID());
                if (points.size() > 0) {
                    Point result = points.get(0);
                    result.setCatID(c.getId());
                }
                tx.commit();

            } finally {
                pm.close();
            }
        }
        return PointModelFactory.createPointModel(point);

    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#addPoint(com.nimbits.client.model.DataPoint, com.nimbits.client.model.PointCatagory, com.nimbits.client.model.user.NimbitsUser)
      */
    @Override
    public Point addPoint(final Point point, final Category c) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Point p = PointServiceFactory.getInstance().getPointByName(u, point.getName());
        Point retObj;

        if (p != null) {
            throw new NimbitsException("A point with the name " + point.getName().getValue() + " already exists");
        } else {
            final DataPoint jdoPoint = new DataPoint(point);
            jdoPoint.setUuid(UUID.randomUUID().toString());
            jdoPoint.setCreateDate(new Date());

            pm.makePersistent(jdoPoint);

            //PointCacheManager.put(jdoPoint);
            retObj = PointModelFactory.createPointModel(jdoPoint);
        }
        pm.close();
        return retObj;
    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#addPoint(java.lang.String, com.nimbits.client.model.PointCatagory, com.nimbits.client.model.user.NimbitsUser, java.lang.String)
      */
    @Override
    public Point addPoint(final PointName pointName, final Category c) throws NimbitsException {

        Point retObj = null;
        Category targetCategory;

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            if (!(pointName == null) && (pointName.getValue().trim().length() > 0)) {

                if (c == null) {
                    CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(Const.CONST_HIDDEN_CATEGORY);
                    targetCategory = CategoryServiceFactory.getInstance().getCategory(u, categoryName);

                    if (targetCategory == null) {
                        // targetCategory = CategoryTransactionFactory.getInstance(u).createHiddenCategory(u);
                        targetCategory = CategoryServiceFactory.getInstance().createHiddenCategory(u);
                    }
                } else {
                    targetCategory = c;
                }


                //check if point exists
                Point pp = PointServiceFactory.getInstance().getPointByName(u, pointName);

                if (pp == null) {

                    final DataPoint jdoPoint = new DataPoint(
                            u.getId(),
                            pointName,
                            targetCategory.getId(),
                            UUID.randomUUID().toString());

                    jdoPoint.setPublic(true);
                    jdoPoint.setCompression(0.1);
                    jdoPoint.setExpire(90);
                    jdoPoint.setLastChecked(new Date());
                    pm.makePersistent(jdoPoint);

                    retObj = PointModelFactory.createPointModel(jdoPoint);

                    //  final Value v = new RecordedValue(0.0, 0.0, 0.0, new Date(new Date().getTime() - 1000 * 60 * 60 * 24), jdoPoint.getId(), Const.DEFAULT_NOTE);


                    // pm.makePersistent(v);

                    //PointCacheManager.put(retObj);

                } else {
                    throw new NimbitsException("A point with the name " + pointName + " already exists");
                }
            }
        } finally {
            pm.close();
        }

        return retObj;

    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#updatePointStats(com.nimbits.client.model.DataPoint, com.nimbits.client.model.value.RecordedValue)
      */
//    @Override
//    public Point updatePointStats(
//            final User u,
//            final Point point,
//            final Value v,
//            boolean alarmSent) {
//
//
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final Transaction tx = pm.currentTransaction();
//
//        Point retObj = null;
//        try {
//            Random generator = new Random();
//            int shardNum = generator.nextInt(SHARD_COUNT); //0-9;
//            RecordedValue recordedValue = new RecordedValue(v, ValueType.statisticShard);
//
//            final DataPoint p = pm.getObjectById(DataPoint.class, point.getId());
//
//            tx.begin();
//            if (p.getPointStatShardEntities().size() - 1 < shardNum) {
//                shardNum = p.getPointStatShardEntities().size();
//                DataPointStatShardEntity newShard = new DataPointStatShardEntity(shardNum);
//
//                newShard.setLowestRecordedValue(recordedValue);
//                newShard.setMostRecentRecordedValue(recordedValue);
//                newShard.setHighestRecordedValue(recordedValue);
//                newShard.setRecordedValueCounter(1);
//                newShard.setPoint(p);
//                if (alarmSent) {
//                    newShard.setMostRecentAlarmSent(new Date());
//                }
//                pm.makePersistent(newShard);
//
//
//            } else {
//                DataPointStatShardEntity shard = p.getPointStatShardEntities().get(shardNum);
//                if (shard.getHighestRecordedValue().getValue() < v.getValue()) {
//                    shard.setHighestRecordedValue(recordedValue);
//                }
//                if (shard.getLowestRecordedValue().getValue() > v.getValue()) {
//                    shard.setLowestRecordedValue(recordedValue);
//                }
//                if (shard.getMostRecentRecordedValue().getTimestamp().getTime() < v.getTimestamp().getTime()) {
//                    shard.setMostRecentRecordedValue(recordedValue);
//                }
//                if (alarmSent) {
//                    shard.setMostRecentAlarmSent(new Date());
//                }
//                shard.setRecordedValueCounter(shard.getRecordedValueCounter() + 1);
//
//            }
//            tx.commit();
//
//            retObj = PointModelFactory.createPointModel(p);
//            //PointCacheManager.remove(p);
//        } catch (ConcurrentModificationException ex) {
//            log.info("Caught attempt to record to the same stat shard at the same time, trying another shard");
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//
//            updatePointStats(u, point, v, alarmSent);
//
//
//        } finally {
//            pm.close();
//        }
//        return retObj;
//
//    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#getPointsByCategory(com.nimbits.client.model.PointCatagory, com.nimbits.client.model.user.NimbitsUser)
      */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<Point> getPointsByCategory(final Category c) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        List<Point> retObj = null;

        long userFK = u.getId();

        try {
            final Query q = pm.newQuery(DataPoint.class, "userFK == k && catID  == c");
            q.declareParameters("Long k, Long c");
            q.setOrdering("name ascending");
            final List<Point> points = (List<Point>) q.execute(userFK, c.getId());
            retObj = PointModelFactory.createPointModels(points);
        } finally {
            pm.close();
        }
        return retObj;
    }


    @Override
    public Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Transaction tx = pm.currentTransaction();
        Point retObj;

        try {
            tx.begin();

            final DataPoint p = pm.getObjectById(DataPoint.class, point.getId());
            p.setLastChecked(new Date());
            p.setHost(req.getServerName());
            if (p.getUUID() == null) {
                p.setUuid(UUID.randomUUID().toString());
            }
            if (p.getCreateDate() == null) {
                p.setCreateDate(new Date());

            }
            p.setAlarmToEmail(p.isAlarmToEmail());


            if (p.getExpire() > 0) {
                TaskFactoryLocator.getInstance().startDeleteDataTask(
                        p.getId(),
                        true, p.getExpire(),
                        p.getName());
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
    * @see com.nimbits.client.service.datapoints.PointTransactions#getAllPoints()
    */
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getAllPoints(int start, int end) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Point> retObj;
        try {

            final Query q = pm.newQuery(DataPoint.class);
            q.declareImports("import java.util.Date");
            q.setOrdering("LastChecked ascending");
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
            List<Point> points;

            Query q = pm
                    .newQuery(DataPoint.class, "idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            points = (List<Point>) q.execute(true, false);
            retObj = PointModelFactory.createPointModels(points);
        } finally {
            pm.close();
        }


        return retObj;

    }

    @Override
    public Point publishPoint(Point point) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Transaction tx = pm.currentTransaction();
        Point retObj;

        try {
            tx.begin();
            {
                final DataPoint p = pm.getObjectById(DataPoint.class, point.getId());
                p.setLastChecked(new Date());
                p.setPublic(true);
                retObj = PointModelFactory.createPointModel(p);
            }
            tx.commit();
            return retObj;
        } finally {
            pm.close();
        }

    }


}


