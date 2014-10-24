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

package com.nimbits.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.value.ValueServiceRpc;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.user.UserHelper;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ValueServiceRpcImpl extends RemoteServiceServlet  implements ValueServiceRpc {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ValueService valueService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private UserHelper userHelper;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public List<Value> solveEquationRpc(final User user, final Calculation calculation) {
        List<Value> response = calculationService.solveEquation(user, calculation, null, null);

        return new ArrayList<>(response);
    }

    @Override
    public Value recordValueRpc(final Entity point,
                                final Value value) throws ValueException {

        User user = userHelper.getUser().get(0);
        HttpServletRequest req = getThreadLocalRequest();
        return valueService.recordValue(req, user, point, value, false);


    }

    @Override
    public Map<String, Entity> getCurrentValuesRpc(final Map<String, Point> entities) throws Exception {
        return valueService.getCurrentValues(entities);

    }

    @Override
    public void createDataDumpRpc(Entity entity, Timespan timespan) {
        User user = userHelper.getUser().get(0);

        taskService.startDataDumpTask(user, entity, timespan);
    }



}
