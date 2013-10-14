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

package com.nimbits.cloudplatform.server.transactions.value.dao;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import org.apache.commons.lang3.Range;

import java.io.IOException;
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

    List<Value> getDataSegment(Entity entity, Range<Long> timespan, Range<Integer> range);

    List<Value> getSeries(Entity entity, Range<Long> timespan);

    List<ValueBlobStore> recordValues(Entity entity, List<Value> values);

    List<ValueBlobStore> getAllStores(Entity entity);

    void consolidateDate(Entity entity, Date timestamp) throws IOException;

    List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key);

    ValueBlobStore mergeTimespan(Entity entity, Range<Date> timespan) throws IOException;

    void purgeValues(Entity entity);

    void deleteExpiredData(Entity entity);

    List<List<Value>> splitUpList(List<Value> original);

    int preloadTimespan(Entity entity, Range timespan) throws Exception;

    List<Value> getPreload(Entity entity, int section);

  //  List<Value> getBuffer(Entity entity);

    void moveValuesFromCacheToStore(Entity entity);
}
