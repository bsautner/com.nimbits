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

package com.nimbits.cloudplatform.server.transactions.value.service;

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.dao.ValueCache;
import org.apache.commons.lang3.Range;

import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.abs;


/**
 * Author: Benjamin Sautner
 * Date: 1/2/13
 * Time: 6:36 PM
 */
public class ValueServiceImpl implements ValueService {
    private final ValueCache cacheService;
    private final EntityService entityService = EntityServiceFactory.getInstance();
    public ValueServiceImpl(PersistenceManagerFactory pmf, NimbitsCache cache) {
        cacheService = ValueServiceFactory.getCacheInstance(pmf, cache);
    }

    @Override
    public List<Value> getTopDataSeries(final Entity entity,
                                        final int maxValues,
                                        final Date endDate)  {
        return cacheService.getTopDataSeries(entity, maxValues, endDate);
    }

    @Override
    public Value recordValue(final User user,
                             final Entity entity,
                             final Value value)  {


        final Point point;

        if (!entity.getEntityType().recordsData()) {
              throw new IllegalArgumentException("You can only record data to a Point. Entity Type was: " + entity.getEntityType().getClassName());

        }

        if (entity instanceof Point) {
            point = (Point) entity;
        } else {
            List<Entity> points = entityService.getEntityByKey(user, entity.getKey(), entity.getEntityType());
            if (!points.isEmpty()) {
                point = (Point) points.get(0);
            } else {
               
                throw new IllegalArgumentException("Point Not Found");
            }
        }

        if (! ignoreByAuthLevel(user, point)) {
            final boolean ignored = false;
            final boolean ignoredByDate = ignoreDataByExpirationDate(point, value, ignored);         
            final boolean ignoredByCompression = ignoreByFilter(point, value);
            final Value retObj;
            List<Value> sample;
            if (!ignoredByDate && !ignoredByCompression) {
                switch (point.getPointType()) {
                    case basic:
                        retObj = cacheService.recordValue(user, point, value);
                        break;

                    case backend:
                        retObj = cacheService.recordValue(user, point, value);
                        break;
                    case cumulative:
                        sample = getPrevValue(point, new Date());
                        if (sample.isEmpty()) {
                            retObj = cacheService.recordValue(user, point, value);
                        } else {
                            Value newValue = ValueModel.getInstance(value, value.getDoubleValue() + sample.get(0).getDoubleValue());
                            retObj = cacheService.recordValue(user, point, newValue);
                        }
                        break;
                    case timespan:
                        retObj = cacheService.recordValue(user, point, value);
                        break;
                    default:
                       throw new IllegalArgumentException("point type not found");

                }

                final AlertType t = getAlertType(point, retObj);
                final Value v = ValueFactory.createValueModel(retObj, t);
                TaskImpl.startRecordValueTask(user, point, v);
                return retObj;
            } else {
                //log.severe("value not recorded");
                return value;   //spit it back I suppose
            }


        }
        else {
        
            return value;
        }
    }

//    private boolean ignoreDataByOwnership(final User user, final Point point, boolean ignored) {
//        //extra safety check to make sure user isn't writing to someone else's point
//        if (user.getId() != point.getUserFK()) {
//            ignored = true;
//        }
//        return ignored;
//    }

    private boolean ignoreDataByExpirationDate(final Point p, final Value value, final boolean ignored) {
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





    @Override
    public List<Value> getDataSegment(Entity entity, Range<Long> timespanRange, Range<Integer> segment) {
        return cacheService.getDataSegment(entity, timespanRange, segment);
    }
    @Override
    public List<Value> getSeries(Entity entity, Range<Long> timespanRange) {
        return cacheService.getSeries(entity, timespanRange);
    }
    private boolean ignoreByAuthLevel(final User u, final Point point)  {

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

    @Override
    public boolean ignoreByFilter(final Point point, final Value v)  {


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
                    return newValue <= max
                            && newValue >= min;

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

    @Override
    public AlertType getAlertType(final Point point, final Value value) {
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

    @Override
    public Map<String, Entity> getCurrentValues(final Map<String, Point> entities) {
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


    @Override
    public List<ValueBlobStore> getAllStores(Entity entity)  {
        return cacheService.getAllStores(entity);
    }


    @Override
    public void purgeValues(Entity entity)  {
        cacheService.purgeValues(entity);

    }


    @Override
    public void deleteExpiredData(Point entity) {
        cacheService.deleteExpiredData(entity);
    }


    @Override
    public void recordValues(User user, Point point, List<Value> values)  {
        if (point.getOwner().equals(user.getKey())) {
            cacheService.recordValues(point, values);
        }
    }


    @Override
    public void moveValuesFromCacheToStore(final Entity entity)  {
        cacheService.moveValuesFromCacheToStore(entity);
    }


    @Override
    public void consolidateDate(final Entity entity, final Date date) throws IOException {
        cacheService.consolidateDate(entity, date);
    }

     @Override
     public void mergeTimespan(Point point, Range<Date> ts) throws IOException {
        cacheService.mergeTimespan(point, ts);
    }

    @Override
    public List<Value> getTopDataSeries(final Entity entity,
                                        final int maxValues) {

        return cacheService.getTopDataSeries(entity, maxValues);

    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan, final int start, final int end)   {
        return cacheService.getDataSegment(entity, Range.between(timespan.getMinimum().getTime(), timespan.getMaximum().getTime()), Range.between(start, end));
    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan)   {
        return cacheService.getDataSegment(entity, Range.between(timespan.getMinimum().getTime(), timespan.getMaximum().getTime()), Range.between(0, 1000));
    }


    @Override
    public Value recordValue(final User u,
                             final EntityName pointName,
                             final Value value)  {


        final List<Entity> e = entityService.getEntityByName(u, pointName, EntityType.point);


        return e.isEmpty() ? null : recordValue(u, e.get(0), value);

    }

    @Override
    public List<Value> getPrevValue(final Entity entity,
                                    final Date timestamp)   {

     
        return cacheService.getRecordedValuePrecedingTimestamp(entity, timestamp);


    }


    @Override
    public double calculateDelta(final Point point)  {
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

    @Override
    public List<Value> getCurrentValue(final Entity p)  {


        if (p != null) {
            final List<Value> retObj = new ArrayList<Value>(1);
            final List<Value> v = getPrevValue(p, new Date());
            if (!v.isEmpty()) {
                final AlertType alertType =  getAlertType((Point) p, v.get(0));
                retObj.add(ValueFactory.createValueModel(v.get(0), alertType));

            }
            return retObj;
        } else {
            
            return Collections.emptyList();
        }


    }

    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(Entity point, Date date) {
        return cacheService.getRecordedValuePrecedingTimestamp(point, date);
    }

    @Override
    public List<Value> getBuffer(Point point) {
        return cacheService.getValueBuffer(point);
    }

    @Override
    public List<Value> getClosestMatchToTimestamp(List<Value> values, Date now) {
        return cacheService.getClosestMatchToTimestamp(values, now);
    }

    @Override
    public List<List<Value>> splitUpList(List<Value> list) {
        return cacheService.splitUpList(list);
    }

}
