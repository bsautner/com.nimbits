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

package com.nimbits.cloudplatform.server.transactions.value;


import com.google.gdata.data.introspection.Collection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.service.value.ValueService;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.calculation.CalculationTransaction;
import com.nimbits.cloudplatform.server.transactions.user.UserHelper;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("valueService")
public class ValueRpcServiceImpl extends RemoteServiceServlet implements ValueService {
    @Override
    public List<Value> solveEquationRpc(final User user, final Calculation calculation)  {
        List<Value> response =  CalculationTransaction.solveEquation(user, calculation);

        return new ArrayList<Value>(response);
    }

    @Override
    public int preloadTimespanRpc(Entity entity, Timespan timespan) throws Exception {
        return ValueMemCache.preloadTimespan(entity, Range.between(timespan.getStart().getTime(), timespan.getEnd().getTime()));
    }

    @Override
    public List<Value> getCacheRpc(final Entity entity) {
        //  final Point point = PointServiceFactory.getInstance().getPointByKey(entity.getKey());
        //  final Point p = (Point) EntityServiceImpl.getEntityByKey(entity.getKey(), PointEntity.class.getName());
        return ValueMemCache.getBuffer(entity);
    }

    @Override
    public List<Value> getPieceOfDataSegmentRpc(final Entity entity,
                                                final Timespan timespan,
                                                final int start,
                                                final int end)   {
        return ValueMemCache.getDataSegment(entity, Range.between(timespan.getStart().getTime(), timespan.getEnd().getTime()), Range.between(start, end));
    }

    @Override
    public Value recordValueRpc(final Entity point,
                                final Value value)  {

        User user = UserHelper.getUser();

        return ValueTransaction.recordValue(user, point, value);


    }

    @Override
    public Map<String, Entity> getCurrentValuesRpc(final Map<String, Point> entities) throws Exception {
        return ValueTransaction.getCurrentValues(entities);

    }

    @Override
    public void createDataDumpRpc(Entity entity, Timespan timespan) {
        TaskImpl.startDataDumpTask(entity, timespan);
    }

    @Override
    public List<Value> getTopDataSeriesRpc(Entity baseEntity, int i, Date date) throws Exception {
        return ValueTransaction.getTopDataSeries(baseEntity, i, date);
    }


}
