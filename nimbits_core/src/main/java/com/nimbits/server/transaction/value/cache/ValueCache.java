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

package com.nimbits.server.transaction.value.cache;

import com.google.common.collect.Range;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;

import java.util.Date;
import java.util.List;


public interface ValueCache {


    List<Value> addValueToBuffer(Entity entity, Value value);

    List<Value> getValueBuffer(Entity entity);

    //todo lookup and delete before put may not be neccesary
    Value recordValue(User user,
                      Entity entity,
                      Value value);

    void addPointToActiveList(Entity point);

    void removePointFromActiveList(Entity point);

    List<Value> getRecordedValuePrecedingTimestamp(Entity entity, Date timestamp);

    List<Value> getClosestMatchToTimestamp(List<Value> values, Date timestamp);

    List<Value> getTopDataSeries(Entity entity, int maxValues);

    List<Value> getTopDataSeries(Entity entity, int maxValues, Date endDate);

    List<Value> getDataSegment(Entity entity, Range<Date> timespan, Range<Integer> range);

    List<Value> getSeries(Entity entity, Range<Date> timespan);

    List<ValueBlobStore> recordValues(Entity entity, List<Value> values);

    List<ValueBlobStore> getAllStores(Entity entity);

    void deleteExpiredData(Entity entity);

    List<List<Value>> splitUpList(List<Value> original);

    void moveValuesFromCacheToStore(Entity entity);

    void consolidateBlobs(Point entity);
}
