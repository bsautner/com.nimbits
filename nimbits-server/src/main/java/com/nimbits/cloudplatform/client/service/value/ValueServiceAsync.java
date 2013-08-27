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

package com.nimbits.cloudplatform.client.service.value;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ValueServiceAsync {
    void solveEquationRpc(final User u, final Calculation calculation, final AsyncCallback<List<Value>> async);

    void recordValueRpc(final Entity point, final Value value, final AsyncCallback<Value> asyncCallback) ;
    void getCacheRpc(final Entity entity, AsyncCallback<List<Value>> async);
    void getPieceOfDataSegmentRpc(final Entity entity, final Timespan timespan, final int start, final int end, AsyncCallback<List<Value>> async);
    void getCurrentValuesRpc(Map<String, Point> entities, AsyncCallback<Map<String, Entity>> async);
    void preloadTimespanRpc(final Entity entity, final Timespan timespan, AsyncCallback<Integer> async);
    void createDataDumpRpc(Entity entity, Timespan timespan, AsyncCallback<Void> asyncCallback);
    void getTopDataSeriesRpc(Entity baseEntity, int i, Date date, AsyncCallback<List<Value>> topSeriesListAsyncCallback);
}
