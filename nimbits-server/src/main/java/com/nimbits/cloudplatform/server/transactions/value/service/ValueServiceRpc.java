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


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.calculation.CalculationTransaction;
import com.nimbits.cloudplatform.server.transactions.user.UserHelper;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.service.ValueService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("valueService")
public class ValueServiceRpc extends RemoteServiceServlet implements com.nimbits.cloudplatform.client.service.value.ValueServiceRpc {
    ValueService valueService = ValueServiceFactory.getInstance();

    @Override
    public List<Value> solveEquationRpc(final User user, final Calculation calculation)  {
        List<Value> response =  CalculationTransaction.solveEquation(user, calculation);

        return new ArrayList<Value>(response);
    }

    @Override
    public Value recordValueRpc(final Entity point,
                                final Value value)  {

        User user = UserHelper.getUser();

        return ValueServiceFactory.getInstance().recordValue(user, point, value);


    }

    @Override
    public Map<String, Entity> getCurrentValuesRpc(final Map<String, Point> entities) throws Exception {
        return ValueServiceFactory.getInstance().getCurrentValues(entities);

    }

    @Override
    public void createDataDumpRpc(Entity entity, Timespan timespan) {
        TaskImpl.startDataDumpTask(entity, timespan);
    }


}
