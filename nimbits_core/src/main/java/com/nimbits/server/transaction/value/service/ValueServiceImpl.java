/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.value.service;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.chart.ChartHelper;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.defrag.Defragmenter;
import com.nimbits.server.defrag.ValueDayHolder;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.abs;

public class ValueServiceImpl implements ValueService {

    private static final Logger logger = LoggerFactory.getLogger(ValueServiceImpl.class.getName());

    private ChartHelper chartHelper;

    private Defragmenter defragmenter;


    public ValueServiceImpl(
                            ChartHelper chartHelper, Defragmenter defragmenter ) {

        this.chartHelper = chartHelper;
        this.defragmenter = defragmenter;

    }

    @Override //process points using system cron, send idle alerts etc.
    public void process(final GeoSpatialDao geoSpatialDao,
                        final TaskService taskService,
                        final UserService userService,
                        final EntityDao entityDao,
                        final ValueTask valueTask,
                        final EntityService entityService,
                        final BlobStore blobStore,
                        final ValueService valueService,
                        final SummaryService summaryService,
                        final SyncService syncService,
                        final SubscriptionService subscriptionService,
                        final CalculationService calculationService,
                        final DataProcessor dataProcessor,
                        final User user, final Point p, final Value value) throws ValueException {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, p.getIdleSeconds() * -1);
       // boolean retVal = false;

        //
        final User u = (User) entityDao.getEntity(user,
                p.getOwner(), EntityType.user).get();


        if (p.getIdleSeconds() > 0 &&
                value.getTimestamp().getTime() <= c.getTimeInMillis() &&
                !p.getIdleAlarmSent()) {
            p.setIdleAlarmSent(true);
            entityService.addUpdateEntity(this, u, p);
            // PointServiceFactory.getInstance().updatePoint(u, p);
            logger.info("DP:: " + this.getClass().getName() + " " + (dataProcessor == null));
            subscriptionService.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore, valueService, summaryService,
                    syncService, subscriptionService, calculationService, dataProcessor, u, p,
                    new Value.Builder().initValue(value).alertType(AlertType.IdleAlert).create()
            );
            //retVal = true;
        }


    }


    @Override
    public String getChartTable(EntityDao entityDao, BlobStore blobStore, User user, Entity entity, Optional<Range<Date>> timespan, Optional<Integer> count, Optional<String> mask) {
        return chartHelper.createChart(entityDao, blobStore, this, user, entity, timespan, count, mask);

    }

    @Override
    public List<Value> getSeries(BlobStore blobStore, Entity entity, Optional<Range<Date>> timespan, final Optional<Range<Integer>> range, Optional<String> mask) {
        List<Value> series = blobStore.getSeries(this, entity, timespan, range, mask);

        return setAlertValues((Point) entity, series);
    }



    @Override
    public AlertType getAlertType(final Point point, final Value value) {
        AlertType retObj = AlertType.OK;

        if (point.isHighAlarmOn() || point.isLowAlarmOn()) {
            if (value.getDoubleValue() != null) {

                if (point.isHighAlarmOn() && value.getDoubleValue() >= point.getHighAlarm()) {
                    retObj = AlertType.HighAlert;
                }
                if (point.isLowAlarmOn() && value.getDoubleValue() <= point.getLowAlarm()) {
                    retObj = AlertType.LowAlert;
                }
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
    public Map<String, Entity> getCurrentValues(BlobStore blobStore, final Map<String, Point> entities) {
        final Map<String, Entity> retObj = new HashMap<>(entities.size());
        for (final Point p : entities.values()) {

            final Value v = getCurrentValue(blobStore, p);

            p.setValue(v);

            retObj.put(p.getId(), p);
        }
        return retObj;

    }


    @Override
    public void recordValues(BlobStore blobStore, User user, Point point, List<Value> values) {
        if (point.getOwner().equals(user.getId())) {

            if (!values.isEmpty() && ! point.getPointType().equals(PointType.location)) {

                storeValues(blobStore, point, values);
            }
            Value value = blobStore.getSnapshot(point);
            Value newer = null;
            for (Value vx : values) {
                if (value.getTimestamp().getTime() < vx.getTimestamp().getTime()) {
                    if (newer != null && newer.getTimestamp().getTime() < vx.getTimestamp().getTime()) {
                        newer = vx;
                    }
                    else if (newer == null) {
                        newer = vx;
                    }

                }
            }
            if (newer != null) {
                blobStore.saveSnapshot(point, newer);
            }

        }
    }

    @Override
    public void storeValues(BlobStore blobStore, Entity entity, List<Value> values) {
        final Map<Long, ValueDayHolder> individualDaysValueMap = defragmenter.getLongValueDayHolderMap(
                ImmutableList.copyOf(values)
        );


        for (final ValueDayHolder longListEntry : individualDaysValueMap.values()) {

            blobStore.createBlobStoreEntity(entity, longListEntry);


        }
    }




    private List<Value> setAlertValues(Point entity, List<Value> series) {
        List<Value> retObj = new ArrayList<>(series.size());
        AlertType alertType;
        for (Value v : series) {
            alertType = getAlertType(entity, v);
            Value vx = new Value.Builder().initValue(v).alertType(alertType).create();
            retObj.add(vx);
        }
        return ImmutableList.copyOf(retObj);
    }



    @Override
    public double calculateDelta(BlobStore blobStore, final Point point) {
        double retVal;

        Calendar compareTime = Calendar.getInstance();
        compareTime.add(Calendar.SECOND, (point.getDeltaSeconds() * -1));
        Range<Date> timespan = Range.closed(compareTime.getTime(), new Date());
        List<Value> series = getSeries(blobStore, point, Optional.of(timespan), Optional.<Range<Integer>>absent(), Optional.<String>absent());

        //Value start = getCurrentValue(blobStore, point);
        double startValue = series.get(series.size() -1).getDoubleValue();
        double endValue = series.get(0).getDoubleValue();

        retVal = abs(startValue - endValue);


        return retVal;
    }

    @Override
    public Value getCurrentValue(BlobStore blobStore, final Entity p) {

        final Value v = blobStore.getSnapshot(p);
        final AlertType alertType = getAlertType((Point) p, v);
        return new Value.Builder().initValue(v).alertType(alertType).create();


    }


}
