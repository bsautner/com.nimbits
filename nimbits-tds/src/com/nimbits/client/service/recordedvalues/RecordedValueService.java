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
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

@RemoteServiceRelativePath("data")
public interface RecordedValueService extends RemoteService {




    List<Value> getCache(final Entity entity) throws NimbitsException;

    Value getCurrentValue(final Entity entity) throws NimbitsException;



    List<Value> getPieceOfDataSegment(final Entity entity, final Timespan timespan, final int start, final int end) throws NimbitsException;

    Value recordValue(final User u, final Entity target, final Value value) throws NimbitsException;

    //rpc
    Value recordValue(final User u, final EntityName pointName, final Value value) throws NimbitsException;

    Value recordValue(final Entity entity, final Value value) throws NimbitsException;


    Value getPrevValue(final Entity p, final Date date) throws NimbitsException;

    Date getLastRecordedDate(final List<Point> points) throws NimbitsException;

 //   List<Value> getTopDataSeries(final Point point, final int maxValues, final Date endDate) throws NimbitsException;

    List<Value> getTopDataSeries(final Entity point, final int maxValues, final Date endDate) throws NimbitsException;

    List<Value> getTopDataSeries(final Entity point, final int maxValues) throws NimbitsException;

    List<Value> getDataSegment(final Entity point,
                               final Timespan timespan,
                               final int start,
                               final int end) throws NimbitsException;

    List<Value> getDataSegment(final Entity point,
                               final Timespan timespan) throws NimbitsException;


    Map<String, Entity> getCurrentValues(Map<String, Point> entities) throws NimbitsException;
}
