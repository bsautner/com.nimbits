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

package com.nimbits.server.transactions;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.ApplicationListener;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.calculation.CalculationServiceFactory;
import com.nimbits.server.transaction.user.UserHelper;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ValueServiceRpc extends RemoteServiceServlet implements com.nimbits.client.service.value.ValueServiceRpc {

    NimbitsEngine engine = ApplicationListener.createEngine();
    TaskService task = ApplicationListener.getTaskService(engine);
    ValueService valueService = ValueServiceFactory.getInstance(engine, task);

    @Override
    public List<Value> solveEquationRpc(final User user, final Calculation calculation) {
        List<Value> response = CalculationServiceFactory.getInstance(engine, task).solveEquation(user, calculation);

        return new ArrayList<Value>(response);
    }

    @Override
    public Value recordValueRpc(final Entity point,
                                final Value value) {

        User user = UserHelper.getUser(engine);

        return valueService.recordValue(user, point, value);


    }

    @Override
    public Map<String, Entity> getCurrentValuesRpc(final Map<String, Point> entities) throws Exception {
        return valueService.getCurrentValues(entities);

    }

    @Override
    public void createDataDumpRpc(Entity entity, Timespan timespan) {
        task.startDataDumpTask(entity, timespan);
    }


}
