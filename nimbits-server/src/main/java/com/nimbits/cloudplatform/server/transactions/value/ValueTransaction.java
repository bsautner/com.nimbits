/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.transactions.value;

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.apache.commons.lang3.Range;

import java.util.*;
import java.util.logging.Logger;

import static java.lang.Math.abs;

/**
 * Author: Benjamin Sautner
 * Date: 1/2/13
 * Time: 6:36 PM
 */
public class ValueTransaction {
    static final Logger log = Logger.getLogger(ValueTransaction.class.getName());
    public static final String MSG = "Could not record value do to permissions levels being to low for a write operation";

    public static List<Value> getTopDataSeries(final Entity entity,
                                               final int maxValues,
                                               final Date endDate) throws Exception {
//        final Point p = (Point) EntityServiceImpl.getEntityByKey(entity.getKey(), PointEntity.class.getName());
        return ValueMemCache.getTopDataSeries(entity, maxValues, endDate);
    }

    public static Value recordValue(final User user,
                                    final Entity entity,
                                    final Value value)  {


        final Point point;

        if (!entity.getEntityType().recordsData()) {
            log.info("attempt to record a value to a non value entity");
            throw new IllegalArgumentException("You can only record data to a Point. Entity Type was: " + entity.getEntityType().getClassName());

        }

        if (entity instanceof Point) {
            point = (Point) entity;
        } else {
            List<Entity> points = EntityServiceImpl.getEntityByKey(user, entity.getKey(), entity.getEntityType());
            if (!points.isEmpty()) {
                point = (Point) points.get(0);
            } else {
                log.info("point not found");
                throw new IllegalArgumentException("Point Not Found");
            }
        }

        if (ignoreByAuthLevel(user, point)) {
            log.info(MSG);
            throw new IllegalArgumentException(MSG);
        } else {

            final boolean ignored = false;
            final boolean ignoredByDate = ignoreDataByExpirationDate(point, value, ignored);
            log.info("ignore by date:" + ignoredByDate);
            final boolean ignoredByCompression = ignoreByFilter(point, value);
            log.info("ignoredByCompression:" + ignoredByCompression);
            final Value retObj;
            List<Value> sample;
            if (!ignoredByDate && !ignoredByCompression) {
                switch (point.getPointType()) {


                    case basic:
                        retObj = ValueMemCache.recordValue(point, value);
                        break;

                    case backend:
                        retObj = ValueMemCache.recordValue(point, value);
                        break;
                    case cumulative:
                        sample = getPrevValue(point, new Date());
                        if (sample.isEmpty()) {
                            retObj = ValueMemCache.recordValue(point, value);
                        } else {
                            Value newValue = ValueModel.getInstance(value, value.getDoubleValue() + sample.get(0).getDoubleValue());
                            retObj = ValueMemCache.recordValue(point, newValue);
                        }
                        break;
                    case timespan:
                        retObj = ValueMemCache.recordValue(point, value);
                        break;
                    default:
                        log.info("point type not found");
                        throw new IllegalArgumentException("point type not found");

                }

                final AlertType t = getAlertType(point, retObj);
                final Value v = ValueFactory.createValueModel(retObj, t);
                TaskImpl.startRecordValueTask(user, point, v);
                return retObj;
            } else {
                //log.severe("value not recorded");
                return value;   //spit it back u suppose
            }


        }
    }

//    private static boolean ignoreDataByOwnership(final User user, final Point point, boolean ignored) {
//        //extra safety check to make sure user isn't writing to someone else's point
//        if (user.getId() != point.getUserFK()) {
//            ignored = true;
//        }
//        return ignored;
//    }

    private static boolean ignoreDataByExpirationDate(final Point p, final Value value, final boolean ignored) {
        boolean retVal = ignored;

        if (p.getExpire() > 0) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, p.getExpire() * -1);
            if (value.getTimestamp().getTime() < c.getTimeInMillis()) {
                retVal = true;
            }
        }
        return retVal;
    }


    public static Date getLastRecordedDate(final List<Point> points) throws Exception {
        Date retVal = null;

        for (final Point p : points) {
            final List<Value> r = ValueMemCache.getTopDataSeries(p, 1);

            if (!r.isEmpty()) {
                Value rx = r.get(0);
                if (retVal == null) {
                    retVal = rx.getTimestamp();
                } else if (retVal.getTime() < rx.getTimestamp().getTime()) {
                    retVal = rx.getTimestamp();
                }
            }

        }
        if (retVal == null) {
            retVal = new Date();
        }

        return retVal;
    }


    public static List<Value> getDataSegment(Entity entity, Range<Long> timespanRange, Range<Integer> segment) throws Exception {
        return ValueMemCache.getDataSegment(entity, timespanRange, segment);
    }

    private static boolean ignoreByAuthLevel(final User u, final Point point)  {

        if (u.isRestricted()) {
            return true;
        }


        for (final AccessKey key : u.getAccessKeys()) {
            if (key.getAuthLevel().equals(AuthLevel.admin)) {
                return false;
            }
            if (key.getScope().equals(point.getKey()) || key.getScope().equals(point.getOwner())) {
                if (key.getAuthLevel().compareTo(AuthLevel.readWritePoint) >= 0) {
                    return false;
                }

            }
        }



        return true;

    }

    public static boolean ignoreByFilter(final Point point, final Value v)  {


        final List<Value> sample = getPrevValue(point, v.getTimestamp());
        if (sample.isEmpty()) {
            return false;
        } else {
            Value pv = sample.get(0);
            switch (point.getFilterType()) {

                case fixedHysteresis:
                    double min =   pv.getDoubleValue() - point.getFilterValue();
                    double max =    pv.getDoubleValue() + point.getFilterValue();
                    double newValue = v.getDoubleValue();
                    boolean inrange = newValue <= max
                            && newValue >= min;
                    return inrange;
                            //&& v.getNote().equals(pv.getNote())
                            //&& v.getLocation().equals(pv.getLocation())
                          //  && v.getData().equals(pv.getData());

                case percentageHysteresis:
                    if (point.getFilterValue() > 0) {
                        final double p = pv.getDoubleValue() * point.getFilterValue() / 100;
                        return v.getDoubleValue() <= pv.getDoubleValue() + p
                                && v.getDoubleValue() >= pv.getDoubleValue() - p;


                    } else {

                        return false;
                    }

                case ceiling:
                    return v.getDoubleValue() >= point.getFilterValue();

                case floor:
                    return v.getDoubleValue() <= point.getFilterValue();

                case none:
                    return false;
                default:
                    return false;
            }


        }


    }

    public static AlertType getAlertType(final Point point, final Value value) {
        AlertType retObj = AlertType.OK;

        if (point.isHighAlarmOn() || point.isLowAlarmOn()) {

            if (point.isHighAlarmOn() && value.getDoubleValue() >= point.getHighAlarm()) {
                retObj = AlertType.HighAlert;
            }
            if (point.isLowAlarmOn() && value.getDoubleValue() <= point.getLowAlarm()) {
                retObj = AlertType.LowAlert;
            }

        }
        if (point.isIdleAlarmOn()) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, point.getIdleSeconds() * -1);

            if (point.getIdleSeconds() > 0 && value != null &&
                    value.getTimestamp().getTime() <= c.getTimeInMillis()) {

                retObj = AlertType.IdleAlert;
            }

        }
        return retObj;

    }

    public static Map<String, Entity> getCurrentValues(final Map<String, Point> entities) throws Exception {
        final Map<String, Entity> retObj = new HashMap<String, Entity>(entities.size());
        for (final Point p : entities.values()) {

            final List<Value> v = getCurrentValue(p);
            if (!v.isEmpty()) {
                p.setValue(v.get(0));

            }
            retObj.put(p.getKey(), p);
        }
        return retObj;

    }


    public static List<ValueBlobStore> getAllStores(Entity entity)  {
        return ValueMemCache.getAllStores(entity);
    }


    public static void purgeValues(Entity entity)  {
        ValueMemCache.purgeValues(entity);

    }


    public static void deleteExpiredData(Point entity) {
        ValueMemCache.deleteExpiredData(entity);
    }


    public static void recordValues(User user, Point point, List<Value> values) throws Exception {
        if (point.getOwner().equals(user.getKey())) {
            ValueMemCache.recordValues(point, values);
        }
    }


    public static void moveValuesFromCacheToStore(Entity entity)  {
        ValueMemCache.moveValuesFromCacheToStore(entity);
    }


    public static void consolidateDate(final Entity entity, final Date date) {
        ValueMemCache.consolidateDate(entity, date);
    }


    public static List<Value> getPreload(Entity entity, int section)  {
        return ValueMemCache.getPreload(entity, section);
    }

    public static void mergeTimespan(Point point, Timespan ts) throws Exception {
        ValueMemCache.mergeTimespan(point, ts);
    }


    public static List<Value> getTopDataSeries(final Entity entity,
                                               final int maxValues) {

        return ValueMemCache.getTopDataSeries(entity, maxValues);

    }

    public static List<Value> getDataSegment(final Entity entity, final Timespan timespan, final int start, final int end) throws Exception {
        return ValueMemCache.getDataSegment(entity, Range.between(timespan.getStart().getTime(), timespan.getEnd().getTime()), Range.between(start, end));
    }

    public static List<Value> getDataSegment(final Entity entity, final Timespan timespan) throws Exception {
        return ValueMemCache.getDataSegment(entity, Range.between(timespan.getStart().getTime(), timespan.getEnd().getTime()), Range.between(0, 1000));
    }


    public static Value recordValue(final User u,
                                    final EntityName pointName,
                                    final Value value) throws Exception {


        final List<Entity> e = EntityServiceImpl.getEntityByName(u, pointName, EntityType.point);


        return e.isEmpty() ? null : recordValue(u, e.get(0), value);

    }

    public static List<Value> getPrevValue(final Entity entity,
                                           final Date timestamp)   {

        log.info("getPrevValue");
        return ValueMemCache.getRecordedValuePrecedingTimestamp(entity, timestamp);


    }


    public static double calculateDelta(final Point point)  {
        double retVal = 0.0;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, (point.getDeltaSeconds() * -1));
        List<Value> startSample = getPrevValue(point, c.getTime());
        double startValue = 0.0;
        if (!startSample.isEmpty()) {
            Value start = startSample.get(0);
            startValue = start.getDoubleValue();
        }
        List<Value> currentSample = getCurrentValue(point);
        if (!currentSample.isEmpty()) {
            Value current = currentSample.get(0);

            retVal = abs(startValue - current.getDoubleValue());
        }

        return retVal;
    }

    public static List<Value> getCurrentValue(final Entity p)  {


        if (p != null) {
            final List<Value> retObj = new ArrayList<Value>(1);
            final List<Value> v = getPrevValue(p, new Date());
            if (!v.isEmpty()) {
                final AlertType alertType = ValueTransaction.getAlertType((Point) p, v.get(0));
                retObj.add(ValueFactory.createValueModel(v.get(0), alertType));

            }
            return retObj;
        } else {
            log.info("getCurrentValue::returning empty list becuase entity was null");
            return Collections.emptyList();
        }


    }

}
