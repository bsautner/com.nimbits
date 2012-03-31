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

package com.nimbits.server.value;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.task.TaskFactory;
import com.nimbits.server.user.UserServiceFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class RecordedValueServiceImpl extends RemoteServiceServlet implements
        RecordedValueService, RequestCallback {


    private static final Logger log = Logger.getLogger(RecordedValueServiceImpl.class.getName());
    private static final long serialVersionUID = 1L;



    @Override
    public Value getCurrentValue(final Entity entity) throws NimbitsException {

        // final User u = UserServiceFactory.getInstance().getAppUserUsingGoogleAuth();
        //  final User pointOwner = UserTransactionFactory.getInstance().getNimbitsUserByID(pointOwnerId);
        final Point p = PointServiceFactory.getInstance().getPointByUUID(entity.getEntity());


        return getCurrentValue(p);


    }

    public List<Value> getTopDataSeries(final Point point,
                                        final int maxValues,
                                        final Date endDate) throws NimbitsException {
        return RecordedValueTransactionFactory.getInstance(point).getTopDataSeries(maxValues, endDate);
    }

    public List<Value> getTopDataSeries(final Entity entity,
                                        final int maxValues,
                                        final Date endDate) throws NimbitsException {
        final Point p = PointServiceFactory.getInstance().getPointByUUID(entity.getEntity());
        return RecordedValueTransactionFactory.getInstance(p).getTopDataSeries(maxValues, endDate);
    }
    //called from RPC Client
    public List<Point> getDataSeries(final List<Point> points,
                                     final Timespan timespan) throws NimbitsException {

        return getDataSeries1(points, timespan);

    }

    @Override
    public List<Value> getCache(final Point point) throws NimbitsException {
        return RecordedValueTransactionFactory.getInstance(point).getBuffer();
    }
    @Override
    public List<Value> getCache(final Entity entity) throws NimbitsException {
        final Point point = PointServiceFactory.getInstance().getPointByUUID(entity.getEntity());
        return RecordedValueTransactionFactory.getInstance(point).getBuffer();
    }

    public List<Value> getPieceOfDataSegment(final Point point,
                                             final Timespan timespan,
                                             final int start,
                                             final int end) throws NimbitsException {

        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    public List<Value> getPieceOfDataSegment(final Entity entity,
                                             final Timespan timespan,
                                             final int start,
                                             final int end) throws NimbitsException {
        final Point point = PointServiceFactory.getInstance().getPointByUUID(entity.getEntity());

        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public Value recordValue(final Entity point,
                             final Value value) throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        final Point px = PointServiceFactory.getInstance().getPointByUUID(point.getEntity());
        return recordValue(u, px, value, false);
    }


    public Point getTopDataSeries(final Point point,
                                  final int maxValues) throws NimbitsException {

        final List<Value> v = RecordedValueTransactionFactory.getInstance(point).getTopDataSeries(maxValues);
        point.setValues(v);
        return point;

    }

    @Override
    public List<Value> getDataSegment(final Point point, final Timespan timespan, final int start, final int end) throws NimbitsException {
        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public List<Value> getDataSegment(final Point point, final Timespan timespan) throws NimbitsException {
        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan);
    }

    //RPC
    public Value recordValue(final User u,
                             final EntityName pointName,
                             final Value value) throws NimbitsException {


        final Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
        final Point point = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

        return (point != null) ? recordValue(u, point, value, false) : null;

    }


    private static List<Point> getDataSeries1(final List<Point> points,
                                              final Timespan timespan) throws NimbitsException {


        for (final Point point : points) {

            point.setValues(RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan));
        }

        return points;
    }

    public Value getPrevValue(final Point point,
                              final Date timestamp) throws NimbitsException {


        return RecordedValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(timestamp);


    }


    public Value getCurrentValue(final Point p) throws NimbitsException {

        Value retObj = null;
        if (p != null) {

            retObj = getPrevValue(p, new Date());
            if (retObj != null) {
                retObj.setAlertType(getAlertType(p, retObj));
            }

        }

        return retObj;

    }
    private static AlertType getAlertType(final Point point, final Value value)  {
        AlertType retObj = AlertType.OK;

        if (point.isHighAlarmOn() || point.isLowAlarmOn()) {

            if (point.isHighAlarmOn() && (value.getDoubleValue() >= point.getHighAlarm())) {
                retObj = AlertType.HighAlert;
            }
            if (point.isLowAlarmOn() && value.getDoubleValue() <= point.getLowAlarm()) {
                retObj = AlertType.LowAlert;
            }

        }
        if (point.isIdleAlarmOn()) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, point.getIdleSeconds() * -1);

            if (point.getIdleSeconds() > 0 && value != null &&
                    value.getTimestamp().getTime() <= c.getTimeInMillis()) {

                retObj = AlertType.IdleAlert;
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
    protected boolean ignoreByCompression(final Point point, final Value v) throws NimbitsException {


        final Value pv = getPrevValue(point, v.getTimestamp());
        if (pv == null) {
            return false;
        }
        else {

            switch (point.getFilterType()) {

                case fixedHysteresis:
                    return ((v.getDoubleValue() <= (pv.getDoubleValue() + point.getFilterValue()))
                            && (v.getDoubleValue() >= (pv.getDoubleValue() - point.getFilterValue()))
                            && (v.getNote().equals(pv.getNote()))
                            && (v.getLatitude() == pv.getLatitude())
                            && (v.getLongitude() == pv.getLongitude())
                            && (v.getData().equals(pv.getData())));

                case percentageHysteresis:
                    if (point.getFilterValue() > 0) {
                        final double p = pv.getDoubleValue() *  (point.getFilterValue() /100);
                        return (v.getDoubleValue() <= (pv.getDoubleValue() + p))
                                && (v.getDoubleValue() >= (pv.getDoubleValue() - p));


                    }
                    else {

                        return false;
                    }

                case ceiling:
                    return (v.getDoubleValue() >= point.getFilterValue());

                case floor:
                    return (v.getDoubleValue() <= point.getFilterValue());

                default:
                    return false;
                case none:
                    return false;
            }



        }


    }

    public Value recordValue(final User u,
                             final Point point,
                             final Value value,
                             final boolean loopFlag) throws NimbitsException {


        //	RecordedValue prevValue = null;
        Value retObj = null;

        final boolean ignored = false;

        final boolean ignoredByDate = ignoreDataByExpirationDate(point, value, ignored);
        final boolean ignoredByOwnership = ignoreDataByOwnership(u, point, ignored);
        final boolean ignoredByCompression = ignoreByCompression(point, value);

        if (!ignoredByDate && !ignoredByOwnership && !ignoredByCompression) {

            retObj = RecordedValueTransactionFactory.getInstance(point).recordValue(value);
            retObj.setAlertType(getAlertType(point, retObj));
            TaskFactory.getInstance().startRecordValueTask(u, point, value, loopFlag);
        }


        return retObj == null ? value : retObj;

    }

    private static boolean ignoreDataByOwnership(final User u, final Point point, boolean ignored) {
        //extra safety check to make sure user isn't writing to someone else's point
        if (u.getId() != point.getUserFK()) {
            ignored = true;
        }
        return ignored;
    }

    private static boolean ignoreDataByExpirationDate(final Point point, final Value value, boolean ignored) {
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
    public Date getLastRecordedDate(final List<Point> points) throws NimbitsException {
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
