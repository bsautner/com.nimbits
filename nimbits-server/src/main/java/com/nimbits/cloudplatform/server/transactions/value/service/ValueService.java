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

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import org.apache.commons.lang3.Range;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface ValueService {

    List<Value> getTopDataSeries(Entity entity, int maxValues, Date endDate);

    Value recordValue(User user, Entity entity, Value value);

    List<Value> getDataSegment(Entity entity, Range<Long> timespanRange, Range<Integer> segment);

    List<Value> getSeries(Entity entity, Range<Long> timespanRange);

    boolean ignoreByFilter(Point point, Value v);

    AlertType getAlertType(Point point, Value value);

    Map<String, Entity> getCurrentValues(Map<String, Point> entities);

    List<ValueBlobStore> getAllStores(Entity entity);

    void purgeValues(Entity entity);

    void deleteExpiredData(Point entity);

    void recordValues(User user, Point point, List<Value> values);

    void moveValuesFromCacheToStore(Entity entity);

    void consolidateDate(Entity entity, Date date) throws IOException;

    void mergeTimespan(Point point, Range<Date> ts) throws IOException;

    List<Value> getTopDataSeries(Entity entity, int maxValues);

    List<Value> getDataSegment(Entity entity, Range<Date> timespan, int start, int end);

    List<Value> getDataSegment(Entity entity, Range<Date> timespan);

    Value recordValue(User u, EntityName pointName, Value value);

    List<Value> getPrevValue(Entity entity, Date timestamp);

    double calculateDelta(Point point);

    List<Value> getCurrentValue(Entity p);

    List<Value> getRecordedValuePrecedingTimestamp(Entity entity, Date date);

    List<Value> getBuffer(Point point);

    List<Value> getClosestMatchToTimestamp(List<Value> values, Date now);

    List<List<Value>> splitUpList(List<Value> list);
}
