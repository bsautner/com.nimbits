/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.valueblobstore.*;

import java.util.*;

public interface ValueTransactions {

    Value getRecordedValuePrecedingTimestamp(final Date timestamp) throws NimbitsException;


    Value recordValue(final Value v) throws NimbitsException;

    // this can throw an exception if the indexes are building on prod
    List<Value> getTopDataSeries(final int maxValues) throws NimbitsException;

    List<Value> getTopDataSeries(final int maxValues,
                                 final Date endDate) throws NimbitsException;

    List<Value> getDataSegment(final Timespan timespan) throws NimbitsException;

    List<Value> getDataSegment(final Timespan timespan,
                               final int start,
                               final int end) throws NimbitsException;

    List<Value> getBuffer() throws NimbitsException;

    List<ValueBlobStore> recordValues(final List<Value> values) throws NimbitsException;

    void moveValuesFromCacheToStore() throws NimbitsException;

    List<Value> getCache(final Timespan timespan) throws NimbitsException;

    List<ValueBlobStore> getAllStores() throws NimbitsException;

    void consolidateDate(Date timestamp) throws NimbitsException;

    List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key) throws NimbitsException;

    ValueBlobStore mergeTimespan(Timespan timespan) throws NimbitsException ;


    void purgeValues() throws NimbitsException;

    void deleteExpiredData();
}