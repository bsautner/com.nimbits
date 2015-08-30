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

package com.nimbits.io.helper.impl;

import com.google.common.collect.Range;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.value.Value;

import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.ValueHelper;
import com.nimbits.io.http.NimbitsClientFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class ValueHelperImpl implements ValueHelper {
    private final Server server;

    public ValueHelperImpl(Server server) {
        this.server = server;

    }


    @Override
    public void recordValue(String pointName, double value) {
        Value vx = new Value.ValueBuilder().doubleValue(value).createValue();
        doRecordValue(pointName, vx);

    }

    @Override
    public void recordValue(String pointName, Value value) {

        doRecordValue(pointName, value);

    }

    private void doRecordValue(String name, Value vx) {
        Point point = HelperFactory.getPointHelper(server).getPoint(name);

        NimbitsClientFactory.getInstance(server).postValue(point, vx);

    }

    @Override
    public List<Value> getSeries(String name) {
        Point point = HelperFactory.getPointHelper(server).getPoint(name);

        return NimbitsClientFactory.getInstance(server).getSeries(point.getKey());

    }

    @Override
    public void recordValue(String name, double v, Date time) {
        Value vx = new Value.ValueBuilder().doubleValue(v).timestamp(time).createValue();
        doRecordValue(name, vx);
    }

    @Override
    public List<Value> getSeries(String name, Range<Date> dateRange) {

        return NimbitsClientFactory.getInstance(server).getSeries(name, dateRange);
    }

    @Override
    public List<Value> getSeries(String name, int count) {

        return NimbitsClientFactory.getInstance(server).getSeries(name, count);
    }

    @Override
    public void recordValues(String pointName, List<Value> data) {
        Point point = HelperFactory.getPointHelper(server).getPoint(pointName);
        point.setValues(data);
        NimbitsClientFactory.getInstance(server).recordSeries(point);
    }

    @Override
    public void recordValues(List<Point> points) {
        NimbitsClientFactory.getInstance(server).recordSeries(points);
    }

    @Override
    public Map<String, Integer> moveCron() {
        return NimbitsClientFactory.getInstance(server).moveCron();


    }

    @Override
    public Value getValue(String name) {
        // Point point = HelperFactory.getPointHelper(server).getPoint(name);
        return NimbitsClientFactory.getInstance(server).getValue(name);
    }


}
