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
import com.google.common.collect.Range;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.value.ValueServiceRpc;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ValueServiceRpcImpl extends RemoteServiceServlet implements ValueServiceRpc {


    @Autowired
    private ValueTask valueTask;

    @Autowired
    private ValueService valueService;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private CalculationService calculationService;


    public ValueServiceRpcImpl() { }

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public String getChartTable(User user, Entity entity, Integer countParam) {
        Optional<Integer> count = (countParam != null && countParam > 0) ? Optional.of(countParam) : Optional.<Integer>absent();
        return valueService.getChartTable(user, entity, Optional.<Range<Long>>absent(), count, Optional.<String>absent());
    }

    @Override
    public List<Value> solveEquationRpc(final User user, final Calculation calculation) {

        Optional<Value> response = calculationService.solveEquation(user, calculation, null, null);

        return response.isPresent() ? Collections.singletonList(response.get()) : Collections.<Value>emptyList();
    }

    @Override
    public void recordValueRpc(final User user, final Entity point,
                               final Value value)  {


        Optional<Entity>  entityOptional = entityDao.getEntity(user, point.getId(), EntityType.point);

        try {
            if (entityOptional.isPresent()) {
                valueTask.process(user, (Point) entityOptional.get(), value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Map<String, Value> getCurrentValuesRpc(final User user, final Map<String, Point> entities)  {
        return valueService.getCurrentValues(entities);

    }


}
