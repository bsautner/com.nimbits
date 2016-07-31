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
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.chart.ChartHelper;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.ValueDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Math.abs;

public class ValueServiceImpl implements ValueService {

    private static final Logger logger = LoggerFactory.getLogger(ValueServiceImpl.class.getName());

    private ChartHelper chartHelper;


    private ValueDao valueDao;


    public ValueServiceImpl(
            ChartHelper chartHelper, ValueDao valueDao) {

        this.chartHelper = chartHelper;

        this.valueDao = valueDao;

    }

    @Override //process points using system cron, send idle alerts etc.
    public void process(final GeoSpatialDao geoSpatialDao,
                        final TaskService taskService,
                        final UserService userService,
                        final EntityDao entityDao,
                        final ValueTask valueTask,
                        final EntityService entityService,
                        final ValueDao valueDao,
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
            subscriptionService.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, valueDao, valueService, summaryService,
                    syncService, subscriptionService, calculationService, dataProcessor, u, p,
                    new Value.Builder().initValue(value).alertType(AlertType.IdleAlert).create()
            );
            //retVal = true;
        }


    }


    @Override
    public String getChartTable( User user, Entity entity, Optional<Range<Date>> timespan, Optional<Integer> count, Optional<String> mask) {
        return chartHelper.createChart( user, entity, timespan, count, mask);

    }

    @Override
    public List<Value> getSeries(Entity entity, Optional<Range<Date>> timespan, final Optional<Range<Integer>> range, Optional<String> mask) {
        List<Value> series = valueDao.getSeries( entity, timespan, range, mask);

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
    public void storeValues(Entity entity, List<Value> values) {
        valueDao.storeValues(entity, values);
    }


    @Override
    public Map<String, Entity> getCurrentValues(final Map<String, Point> entities) {
        final Map<String, Entity> retObj = new HashMap<>(entities.size());
        for (final Point p : entities.values()) {

            final Value v = getCurrentValue(p);

            p.setValue(v);

            retObj.put(p.getId(), p);
        }
        return retObj;

    }


    @Override
    public void recordValues(User user, Point point, List<Value> values) {


        for (Value value : values) {
            valueDao.setSnapshot(point, value);
        }

    }



    @Override
    public Value getSnapshot(Point point) {
        return valueDao.getSnapshot(point);
    }

    @Override
    public void deleteAllData(Point point) {

        //TODO OOMA
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
    public double calculateDelta(final Point point) {
        double retVal;

        Calendar compareTime = Calendar.getInstance();
        compareTime.add(Calendar.SECOND, (point.getDeltaSeconds() * -1));
        Range<Date> timespan = Range.closed(compareTime.getTime(), new Date());
        List<Value> series = getSeries(point, Optional.of(timespan), Optional.<Range<Integer>>absent(), Optional.<String>absent());

        //Value start = getCurrentValue(blobStore, point);
        double startValue = series.get(series.size() -1).getDoubleValue();
        double endValue = series.get(0).getDoubleValue();

        retVal = abs(startValue - endValue);


        return retVal;
    }

    @Override
    public Value getCurrentValue(final Entity p) {

        final Value v = valueDao.getSnapshot(p);
        final AlertType alertType = getAlertType((Point) p, v);
        return new Value.Builder().initValue(v).alertType(alertType).create();


    }


}
