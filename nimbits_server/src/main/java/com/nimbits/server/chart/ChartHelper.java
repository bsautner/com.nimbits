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

package com.nimbits.server.chart;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.value.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChartHelper {



    private EntityDao entityDao;
    private ValueDao blobStore;
    private ValueService valueService;

    public ChartHelper(EntityDao entityDao, ValueDao blobStore, ValueService valueService) {
        this.entityDao = entityDao;
        this.blobStore = blobStore;
        this.valueService = valueService;
    }

    public String createChart(User user, Entity entity, Optional<Range<Date>> timespan, Optional<Integer> count, Optional<String> mask) {


        final List<Entity> list = getList(entityDao, user, entity);

        ChartDTO dto = createChartData(blobStore, valueService, list, timespan, count, mask);

        Gson gson =  GsonFactory.getInstance(true);

        return gson.toJson(dto);


    }




    private List<Entity> getList(EntityDao entityDao, User user, Entity entity) {
        final List<Entity> list;

        if (entity.getEntityType().equals(EntityType.point)) {
            list = Collections.singletonList(entity);
        } else if (entity.getEntityType().equals(EntityType.category)) {
            List<Entity> children = entityDao.getChildren(user, Collections.singletonList(entity));
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


    private ChartDTO createChartData(ValueDao blobStore,
                                     ValueService valueService,
                                     List<Entity> points,
                                     Optional<Range<Date>> timespan,
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


        Set<Date> timestamps = new HashSet<>();
        Table<Entity, Date, Value> sensorTable = HashBasedTable.create();
        Optional<Range<Integer>> range;
        if (count.isPresent()) {
            range = Optional.of(Range.closed(0, count.get()));
        }
        else {
            range = Optional.absent();
        }

        for (Entity point : points) {
            List<Value> values  = valueService.getSeries(point, timespan, range, mask);



            for (Value value : values) {

                if (value.getTimestamp().getTime() != new Date().getTime()) { //don't chart the 1970 init value
                    timestamps.add(value.getTimestamp());


                    sensorTable.put(point, value.getTimestamp(), value);
                }

            }


        }


        for (Date timestamp : timestamps) {
            List<ChartDataColumn> chartDataColumns = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timestamp);
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



    private void addSensorData(Table<Entity, Date, Value> sensorTable, Date timestamp, List<ChartDataColumn> chartDataColumns, Entity point) {
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
