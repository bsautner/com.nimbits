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

package com.nimbits.server.io.blob;


import com.google.common.collect.Range;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.transaction.value.dao.ValueDayHolder;

import java.util.Date;
import java.util.List;

public interface BlobStore {

    List<Value> getTopDataSeries(Entity entity, int maxValues, Date endDate);

    List<Value> getDataSegment(Entity entity, Range<Date> timespan);

    List<ValueBlobStore> getAllStores(Entity entity);

    List<Value> consolidateDate(Entity entity, Date timestamp);

    void deleteExpiredData(Entity entity);

    List<Value> readValuesFromFile(String key, long length);

    void deleteBlobs(List<ValueBlobStore> result);

    void delete(String key);

    List<ValueBlobStore> createBlobStoreEntity(Entity entity, ValueDayHolder holder);

    List<ValueBlobStore> mergeTimespan(Entity entity, Range<Date> timespan);

    void delete(List<ValueBlobStore> result);
}
