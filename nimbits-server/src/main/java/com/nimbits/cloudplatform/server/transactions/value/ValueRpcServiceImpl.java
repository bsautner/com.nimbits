/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.transactions.value;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.service.value.ValueService;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserHelper;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("valueService")
public class ValueRpcServiceImpl extends RemoteServiceServlet implements ValueService {


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
