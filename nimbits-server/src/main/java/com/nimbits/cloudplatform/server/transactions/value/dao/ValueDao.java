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
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import org.apache.commons.lang3.Range;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public interface ValueDao {
    List<ValueBlobStore> createValueBlobStores(Collection<ValueBlobStore> store);

    ValueBlobStore createValueBlobStore(ValueBlobStore store);

    List<Value> getRecordedValuePrecedingTimestamp(Entity entity, Date timestamp);

    List<Value> getTopDataSeries(Entity entity, int maxValues);

    List<Value> getTopDataSeries(Entity entity, int maxValues, Date endDate);

    List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key);

    List<Value> getDataSegment(Entity entity, Range<Long> timespan);

    List<ValueBlobStore> getAllStores(Entity entity);

    void consolidateDate(Entity entity, Date timestamp) throws IOException;

    ValueBlobStore mergeTimespan(Entity entity, Range<Date> timespan) throws IOException;

    void purgeValues(Entity entity);

    void deleteExpiredData(Entity entity);

    List<ValueBlobStore> recordValues(Entity entity, List<Value> values);

    List<Value> readValuesFromFile(BlobKey blobKey, long length);

}
