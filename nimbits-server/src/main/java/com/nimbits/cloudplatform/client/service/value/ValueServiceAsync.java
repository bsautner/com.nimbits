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

package com.nimbits.cloudplatform.client.service.value;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ValueServiceAsync {

    void recordValueRpc(final Entity point, final Value value, final AsyncCallback<Value> asyncCallback) ;
    void getCacheRpc(final Entity entity, AsyncCallback<List<Value>> async);
    void getPieceOfDataSegmentRpc(final Entity entity, final Timespan timespan, final int start, final int end, AsyncCallback<List<Value>> async);
    void getCurrentValuesRpc(Map<String, Point> entities, AsyncCallback<Map<String, Entity>> async);
    void preloadTimespanRpc(final Entity entity, final Timespan timespan, AsyncCallback<Integer> async);
    void createDataDumpRpc(Entity entity, Timespan timespan, AsyncCallback<Void> asyncCallback);
    void getTopDataSeriesRpc(Entity baseEntity, int i, Date date, AsyncCallback<List<Value>> topSeriesListAsyncCallback);
}
