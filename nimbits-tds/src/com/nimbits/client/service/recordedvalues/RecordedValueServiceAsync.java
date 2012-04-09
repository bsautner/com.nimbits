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

package com.nimbits.client.service.recordedvalues;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

public interface RecordedValueServiceAsync {

    void getTopDataSeries(final Entity point, final int maxValues,
                          final AsyncCallback<List<Value>> asyncCallback);



    void getPrevValue(final Entity p, final Date date, final AsyncCallback<Value> callback);

    void recordValue(final User u, final EntityName pointName, final Value value, final AsyncCallback<Value> callback);

    void recordValue(final Entity point, final Value value, final AsyncCallback<Value> asyncCallback);

    void getLastRecordedDate(final List<Point> points, final AsyncCallback<Date> callback);

   // void getTopDataSeries(final Point point, int maxValues, final Date endDate, final AsyncCallback<List<Value>> async);

    void getTopDataSeries(final Entity entity, int maxValues, final Date endDate, final AsyncCallback<List<Value>> async);

    void getCurrentValue(Entity entity, final AsyncCallback<Value> async);

    void recordValue(final User u, final Entity target, final Value value, final boolean loopFlag, AsyncCallback<Value> async);

    void getDataSegment(final Entity point,
                        final Timespan timespan,
                        final int start,
                        final int end, AsyncCallback<List<Value>> async);

    void getDataSegment(final Entity point,
                        final Timespan timespan, AsyncCallback<List<Value>> async);


    //void getCache(final Point point, AsyncCallback<List<Value>> async);

    void getCache(final Entity entity, AsyncCallback<List<Value>> async);

    void getPieceOfDataSegment(final Entity entity, final Timespan timespan, final int start, final int end, AsyncCallback<List<Value>> async);
}
