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

package com.nimbits.helper;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.io.HttpClientImpl;

import java.util.Date;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 7:30 PM
 */
public class ValueHelper {
    private final Server server;
    private final EmailAddress email;
    public ValueHelper(Server server, EmailAddress email) {
        this.server = server;
        this.email = email;
    }

    public Value recordValue(String name, double value)  {
        Value vx = ValueFactory.createValueModel(value);
        return doRecordValue(name, vx);

    }

    private Value doRecordValue(String name, Value vx) {
        Point point = new PointHelper(server, email).getPoint(name);

        List<Value> response = new HttpClientImpl(server, email).postValue(point, vx);
        if (response.isEmpty()) {
            throw new RuntimeException("Record Value Failed");

        }
        else {
            return response.get(0);
        }
    }

    public List<Value> getSeries(String name)   {
        Point point = new PointHelper(server, email).getPoint(name);

            return new HttpClientImpl(server, email).getSeries(point.getKey());

    }

    public Value recordValue(String name, double v, Date time) {
        Value vx = ValueFactory.createValueModel(v, time);
        return doRecordValue(name, vx);
    }
}
