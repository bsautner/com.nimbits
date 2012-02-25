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

package com.nimbits.server.recordedvalue;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;

import java.util.*;

public interface RecordedValueTransactions {

    Value getRecordedValuePrecedingTimestamp(final Date timestamp);


    Value recordValue(final Value v) ;

    // this can throw an exception if the indexes are building on prod
    List<Value> getTopDataSeries(final int maxValues);

    List<Value> getTopDataSeries(final int maxValues,
                                 final Date endDate);

    List<Value> getDataSegment(final Timespan timespan);

    List<Value> getDataSegment(final Timespan timespan,
                               final int start,
                               final int end);

    List<Value> getCache();

    void recordValues(final List<Value> values);

    void moveValuesFromCacheToStore() throws NimbitsException;

    List<Value> getCache(final Timespan timespan) throws NimbitsException;
}