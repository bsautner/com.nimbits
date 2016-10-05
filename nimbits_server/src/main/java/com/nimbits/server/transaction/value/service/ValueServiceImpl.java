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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.chart.ChartColumnDefinition;
import com.nimbits.server.chart.ChartDTO;
import com.nimbits.server.chart.ChartDataColumn;
import com.nimbits.server.chart.Row;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.value.ValueDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ValueServiceImpl implements ValueService {

    private final ValueDao valueDao;

    @Autowired
    public ValueServiceImpl(ValueDao valueDao) {

        this.valueDao = valueDao;

    }


    @Override
    public String getChartTable(User user, Entity entity, List<Entity> children, Optional<Range<Long>> timespan, Optional<Integer> count, Optional<String> mask) {
        return createChart(user, entity, children, timespan, count, mask);

    }

    @Override
    public List<Value> getSeries(Entity entity, Optional<Range<Long>> timespan, final Optional<Range<Integer>> range, Optional<String> mask) {
        List<Value> series = valueDao.getSeries(entity, timespan, range, mask);

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


            if (point.getIdleSeconds() > 0 && value != null &&
                    value.getLTimestamp() <= System.currentTimeMillis() - (point.getIdleSeconds() * 1000) ) {

                retObj = AlertType.IdleAlert;
            }

        }
        return retObj;

    }


    @Override
    public Map<String, Value> getCurrentValues(final Map<String, Point> entities) {
        final Map<String, Value> retObj = new HashMap<>(entities.size());
        for (final Entity p : entities.values()) {

            final Value v = getCurrentValue(p);


            retObj.put(p.getId(), v);
        }
        return retObj;

    }


    @Override
    public void recordValues(User user, Point point, List<Value> values) {

        valueDao.storeValues(point, values);


    }


    @Override
    public Value getSnapshot(Point point) {
        return valueDao.getSnapshot(point);
    }

    @Override
    public void deleteAllData(Point point) {

        valueDao.deleteAllData(point);
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
    public Value getCurrentValue(final Entity p) {

        final Value v = valueDao.getSnapshot(p);
        final AlertType alertType = getAlertType((Point) p, v);
        return new Value.Builder().initValue(v).alertType(alertType).create();


    }


    private String createChart(User user, Entity entity, List<Entity> children, Optional<Range<Long>> timespan, Optional<Integer> count, Optional<String> mask) {


        final List<Entity> list = getList(user, entity, children);

        ChartDTO dto = createChartData(list, timespan, count, mask);

        Gson gson = GsonFactory.getInstance(true);

        return gson.toJson(dto);


    }


    private List<Entity> getList(User user, Entity entity,  List<Entity> children) {
        final List<Entity> list;

        if (entity.getEntityType().equals(EntityType.point)) {
            list = Collections.singletonList(entity);
        } else if (entity.getEntityType().equals(EntityType.category)) {

            list = new ArrayList<>(children.size());
            for (Entity child : children) {
                if (child.getEntityType().equals(EntityType.point)) {
                    list.add(child);
                }
            }

        } else {
            list = Collections.emptyList();
        }

        return list;


    }


    private ChartDTO createChartData(List<Entity> points,
                                     Optional<Range<Long>> timespan,
                                     Optional<Integer> count,
                                     Optional<String> mask) {
        ChartDTO dto = new ChartDTO();
        List<ChartColumnDefinition> cols = new ArrayList<>();

        ChartColumnDefinition dateCol = new ChartColumnDefinition();
        dateCol.setLabel("Date");
        dateCol.setType("date");
        cols.add(dateCol);
        for (Entity point : points) {

            ChartColumnDefinition dataColumn = new ChartColumnDefinition();
            dataColumn.setLabel(point.getName().getValue());
            dataColumn.setType("number");
            cols.add(dataColumn);


        }


        dto.setCols(cols);

        List<Row> rows = new ArrayList<>();


        Set<Long> timestamps = new HashSet<>();
        Table<Entity, Long, Value> sensorTable = HashBasedTable.create();
        Optional<Range<Integer>> range;
        if (count.isPresent()) {
            range = Optional.of(Range.closed(0, count.get()));
        } else {
            range = Optional.absent();
        }

        for (Entity point : points) {
            List<Value> values = getSeries(point, timespan, range, mask);


            for (Value value : values) {

                if (value.getLTimestamp() != new Date().getTime()) { //don't chart the 1970 init value
                    timestamps.add(value.getLTimestamp());


                    sensorTable.put(point, value.getLTimestamp(), value);
                }

            }


        }


        for (Long timestamp : timestamps) {
            List<ChartDataColumn> chartDataColumns = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            ChartDataColumn dateEntry = new ChartDataColumn(
                    "Date("
                            + calendar.get(Calendar.YEAR) + "," +
                            +calendar.get(Calendar.MONTH) + "," +
                            +calendar.get(Calendar.DAY_OF_MONTH) + "," +
                            +calendar.get(Calendar.HOUR_OF_DAY) + "," +
                            +calendar.get(Calendar.MINUTE) + "," +
                            +calendar.get(Calendar.SECOND) +
                            ")");
            chartDataColumns.add(dateEntry);


            for (Entity item : points) {

                addSensorData(sensorTable, timestamp, chartDataColumns, item);


            }


            Row row = new Row();
            row.setC(chartDataColumns);
            rows.add(row);
        }

        dto.setRows(rows);
        dto.setP(1);
        return dto;
    }


    private void addSensorData(Table<Entity, Long, Value> sensorTable, Long timestamp, List<ChartDataColumn> chartDataColumns, Entity point) {
        ChartDataColumn dataColumn;
        if (sensorTable.contains(point, timestamp)) {

            Value value = sensorTable.get(point, timestamp);
            try {


                dataColumn = new ChartDataColumn(value.getDoubleValue());

            } catch (NumberFormatException ex) {
                dataColumn = new ChartDataColumn(null);
            }

        } else {
            dataColumn = new ChartDataColumn(null);
        }

        chartDataColumns.add(dataColumn);
    }


}
