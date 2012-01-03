/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.recordedvalues;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.CalculationFailedException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.Date;
import java.util.List;

@RemoteServiceRelativePath("data")
public interface RecordedValueService extends RemoteService {

    List<Point> getDataSeries(final List<Point> symbol, final Timespan timespan);

    List<Value> getCache(final Point point);

    Value getCurrentValue(final long pointOwnerId, final PointName pointName) throws NimbitsException;

    Value getCurrentValue(final Point p) throws NimbitsException;

    double solveEquation(final Point point) throws CalculationFailedException, NimbitsException;

    List<Value> getPieceOfDataSegment(final Point point, final Timespan timespan, final int start, final int end);


    Value recordValue(final User u, final Point target, final Value value, final boolean loopFlag) throws NimbitsException;

    //rpc
    Value recordValue(final User u, final PointName pointName, final Value value) throws NimbitsException;

    Value recordValue(final Point point, final Value value) throws NimbitsException;


    Value getPrevValue(final Point p, final Date date);

    Date getLastRecordedDate(final List<Point> points);

    List<Value> getTopDataSeries(final Point point, final int maxValues, final Date endDate);

    Point getTopDataSeries(final Point point, final int maxValues);

    List<Value> getDataSegment(final Point point,
                               final Timespan timespan,
                               final int start,
                               final int end);

    List<Value> getDataSegment(final Point point,
                               final Timespan timespan);
}
