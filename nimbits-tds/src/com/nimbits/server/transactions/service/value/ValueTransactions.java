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

package com.nimbits.server.transactions.service.value;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;

import java.util.Date;
import java.util.List;

public interface ValueTransactions {

    List<List<Value>> splitUpList(List<Value> original) throws NimbitsException;

    List<Value> getRecordedValuePrecedingTimestamp(final Entity entity,final Date timestamp) throws NimbitsException;


    Value recordValue(final Entity entity, final Value v) throws NimbitsException;

    // this can throw an exception if the indexes are building on prod
    List<Value> getTopDataSeries(final Entity entity,final int maxValues) throws NimbitsException;

    List<Value> getTopDataSeries(final Entity entity,
                                 final int maxValues,
                                 final Date endDate) throws NimbitsException;

    List<Value> getDataSegment(final Entity entity,final Timespan timespan) throws NimbitsException;

    List<Value> getDataSegment(final Entity entity, final Timespan timespan,
                               final int start,
                               final int end) throws NimbitsException;

    List<Value> getBuffer(final Entity entity) throws NimbitsException;

    List<ValueBlobStore> recordValues(final Entity entity, final List<Value> values) throws NimbitsException;

    void moveValuesFromCacheToStore(final Entity entity) throws NimbitsException;

    List<Value> getCache(final Entity entity, final Timespan timespan) throws NimbitsException;

    List<ValueBlobStore> getAllStores(final Entity entity) throws NimbitsException;

    void consolidateDate(final Entity entity, Date timestamp) throws NimbitsException;

    List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key) throws NimbitsException;

    ValueBlobStore mergeTimespan(final Entity entity,Timespan timespan) throws NimbitsException ;


    void purgeValues(final Entity entity) throws NimbitsException;

    void deleteExpiredData(final Entity entity);

    int preloadTimespan(final Entity entity,Timespan timespan) throws NimbitsException;

    List<Value> getPreload(final Entity entity, int count) throws NimbitsException;


}