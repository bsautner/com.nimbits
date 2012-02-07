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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.CalculationFailedException;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.server.math.EquationSolver;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.task.TaskFactoryLocator;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.user.UserTransactionFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class RecordedValueServiceImpl extends RemoteServiceServlet implements
        RecordedValueService, RequestCallback {

    private static final Logger log = Logger.getLogger(RecordedValueServiceImpl.class.getName());
    private static final long serialVersionUID = 1L;


    //Section - Calls from RPC Client
    @Override
    public double solveEquation(final Point point) throws CalculationFailedException, NimbitsException {

        final User u = UserServiceFactory.getInstance().getAppUserUsingGoogleAuth();

        return EquationSolver.solveEquation(point, u);
    }


    @Override
    public Value getCurrentValue(final long pointOwnerId,
                                 final EntityName pointName) throws NimbitsException {

        final User u = UserServiceFactory.getInstance().getAppUserUsingGoogleAuth();
        final User pointOwner = UserTransactionFactory.getInstance().getNimbitsUserByID(pointOwnerId);
        final Point p = PointServiceFactory.getInstance().getPointByName(pointOwner, pointName);


        return (PointServiceFactory.getInstance().checkPointProtection(u, pointOwner, p)) ? getCurrentValue(p) : null;


    }

    public List<Value> getTopDataSeries(final Point point,
                                        final int maxValues,
                                        final Date endDate) {
        return RecordedValueTransactionFactory.getInstance(point).getTopDataSeries(maxValues, endDate);
    }


    //called from RPC Client
    public List<Point> getDataSeries(final List<Point> points,
                                     final Timespan timespan) {

        return getDataSeries1(points, timespan);


    }

    @Override
    public List<Value> getCache(Point point) {
        return RecordedValueTransactionFactory.getInstance(point).getCache();
    }


    public List<Value> getPieceOfDataSegment(final Point point,
                                             final Timespan timespan,
                                             final int start,
                                             final int end) {

        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public Value recordValue(final Point point,
                             final Value value) throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        final Point px = PointServiceFactory.getInstance().getPointByID(u, point.getId());
        return recordValue(u, px, value, false);


    }


    public Point getTopDataSeries(final Point point,
                                  final int maxValues) {

        final List<Value> v = RecordedValueTransactionFactory.getInstance(point).getTopDataSeries(maxValues);
        point.setValues(v);
        return point;

    }

    @Override
    public List<Value> getDataSegment(Point point, Timespan timespan, int start, int end) {
        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public List<Value> getDataSegment(Point point, Timespan timespan) {
        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan);
    }

    //RPC
    public Value recordValue(final User u,
                             final EntityName pointName,
                             final Value value) throws NimbitsException {


        final Point point = PointServiceFactory.getInstance().getPointByName(u, pointName);

        return (point != null) ? recordValue(u, point, value, false) : null;

    }


    private List<Point> getDataSeries1(final List<Point> points,
                                       final Timespan timespan) {


        for (final Point point : points) {

            point.setValues(RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan));
        }

        return points;
    }

    public Value getPrevValue(final Point point,
                              final Date timestamp) {


        return RecordedValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(timestamp);


    }


    public Value getCurrentValue(final Point p) throws NimbitsException {

        Value retObj = null;
        if (p != null) {

            retObj = getPrevValue(p, new Date());
            if (retObj != null) {
                retObj.setAlertState(PointServiceFactory.getInstance().getPointAlertState(p, retObj));
            }

        }

        return retObj;

    }


    @Override
    public void onResponseReceived(final Request request, final Response response) {


    }

    @Override
    public void onError(final Request request, final Throwable exception) {


    }


    //determines if a new value should be ignored
    private boolean ignoreByCompression(final User u,
                                        final Point p,
                                        final Value v) {

        boolean r = false;


        if (p.getCompression() > 0) {
            final Value pv = getPrevValue(p, v.getTimestamp());
            if (pv == null) {
                r = false;
            } else if ((v.getNumberValue() <= (pv.getNumberValue() + p.getCompression()))
                    && (v.getNumberValue() >= (pv.getNumberValue() - p.getCompression()))
                    && (v.getNote().equals(pv.getNote()))
                    && (v.getLatitude() == pv.getLatitude())
                    && (v.getLongitude() == pv.getLongitude())
                    && (v.getData().equals(pv.getData()))) {
                r = true; //values are the same within compression setting.
            }
        }


        return r;

    }

    public Value recordValue(final User u,
                             final Point point,
                             final Value value,
                             final boolean loopFlag) throws NimbitsException {


        //	RecordedValue prevValue = null;
        Value retObj = null;

        boolean ignored = false;

        final boolean ignoredByDate = ignoreDataByExpirationDate(point, value, ignored);
        final boolean ignoredByOwnership = ignoreDataByOwnership(u, point, ignored);
        final boolean ignoredByCompression = ignoreByCompression(u, point, value);

        if (!ignoredByDate && !ignoredByOwnership && !ignoredByCompression) {

            retObj = RecordedValueTransactionFactory.getInstance(point).recordValue(value);

            TaskFactoryLocator.getInstance().startRecordValueTask(u, point, value, loopFlag);
        }
        //ChannelService channelService = ChannelServiceFactory.getChannelService();
        if (retObj != null) {
            retObj.setAlertState(PointServiceFactory.getInstance().getPointAlertState(point, retObj));
        }

        return retObj == null ? value : retObj;

    }

    private boolean ignoreDataByOwnership(User u, Point point, boolean ignored) {
        //extra safety check to make sure user isn't writing to someone else's point
        if (u.getId() != point.getUserFK()) {
            ignored = true;
        }
        return ignored;
    }

    private boolean ignoreDataByExpirationDate(final Point point, final Value value, boolean ignored) {
        if (point.getExpire() > 0) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, point.getExpire() * -1);
            if (value.getTimestamp().getTime() < c.getTimeInMillis()) {
                ignored = true;
            }
        }
        return ignored;
    }


    @Override
    public Date getLastRecordedDate(final List<Point> points) {
        Date retVal = null;
        Value rx;

        for (final Point p : points) {
            final List<Value> r = RecordedValueTransactionFactory.getInstance(p).getTopDataSeries(1);

            if (r.size() > 0) {
                rx = r.get(0);
                if (retVal == null) {
                    retVal = rx.getTimestamp();
                } else if (retVal.getTime() < rx.getTimestamp().getTime()) {
                    retVal = rx.getTimestamp();
                }
            }

        }
        if (retVal == null) {
            retVal = new Date();
        }

        return retVal;
    }

}
