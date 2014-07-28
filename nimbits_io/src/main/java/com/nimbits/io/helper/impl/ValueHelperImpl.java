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
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.ValueHelper;
import com.nimbits.io.http.NimbitsClientFactory;

import java.util.Date;
import java.util.List;


public class ValueHelperImpl implements ValueHelper {
    private final Server server;
    private final EmailAddress email;
    private final String accessKey;
    public ValueHelperImpl(Server server, EmailAddress email, String accessKey) {
        this.server = server;
        this.email = email;
        this.accessKey = accessKey;
    }

    @Override
    public Value recordValue(String pointName, double value)  {
        Value vx = ValueFactory.createValueModel(value);
        return doRecordValue(pointName, vx);

    }

    @Override
    public Value recordValue(String pointName, Value value)  {

        return doRecordValue(pointName, value);

    }

    private Value doRecordValue(String name, Value vx) {
        Point point = HelperFactory.getPointHelper(server, email, accessKey).getPoint(name);

        List<Value> response = NimbitsClientFactory.getInstance(server, email, accessKey).postValue(point, vx);
        if (response.isEmpty()) {
            throw new RuntimeException("Record Value Failed");

        }
        else {
            return response.get(0);
        }
    }

    @Override
    public List<Value> getSeries(String name)   {
        Point point = HelperFactory.getPointHelper(server, email, accessKey).getPoint(name);

        return NimbitsClientFactory.getInstance(server, email, accessKey).getSeries(point.getKey());

    }

    @Override
    public Value recordValue(String name, double v, Date time) {
        Value vx = ValueFactory.createValueModel(v, time);
        return doRecordValue(name, vx);
    }

    @Override
    public List<Value> getSeries(String name, Range<Date> dateRange) {

        return NimbitsClientFactory.getInstance(server, email, accessKey).getSeries(name, dateRange);
    }

    @Override
    public List<Value> getSeries(String name, int count) {

        return NimbitsClientFactory.getInstance(server, email, accessKey).getSeries(name, count);
    }
}
