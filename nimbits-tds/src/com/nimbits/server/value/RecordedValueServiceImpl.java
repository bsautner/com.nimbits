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

import com.google.gwt.http.client.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.recordedvalues.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.task.*;
import com.nimbits.server.user.*;

import java.util.*;


public class RecordedValueServiceImpl extends RemoteServiceServlet implements
        RecordedValueService, RequestCallback {

    private static final long serialVersionUID = 1L;





    @Override
    public List<Value> getTopDataSeries(final Entity entity,
                                        final int maxValues,
                                        final Date endDate) throws NimbitsException {
//        final Point p = (Point) EntityServiceFactory.getInstance().getEntityByKey(entity.getKey(), PointEntity.class.getName());
        return RecordedValueTransactionFactory.getInstance(entity).getTopDataSeries(maxValues, endDate);
    }



    @Override
    public List<Value> getCache(final Entity entity) throws NimbitsException {
      //  final Point point = PointServiceFactory.getInstance().getPointByKey(entity.getKey());
      //  final Point p = (Point) EntityServiceFactory.getInstance().getEntityByKey(entity.getKey(), PointEntity.class.getName());
        return RecordedValueTransactionFactory.getInstance(entity).getBuffer();
    }



    @Override
    public List<Value> getPieceOfDataSegment(final Entity entity,
                                             final Timespan timespan,
                                             final int start,
                                             final int end) throws NimbitsException {
          return RecordedValueTransactionFactory.getInstance(entity).getDataSegment(timespan, start, end);
    }

    @Override
    public Value recordValue(final Entity point,
                             final Value value) throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

//        final Point px = PointServiceFactory.getInstance().getPointByKey(point.getKey());
     //   final Point px = (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), PointEntity.class.getName());
        return recordValue(u,point, value, false);
    }


    @Override
    public List<Value> getTopDataSeries(final Entity point,
                                  final int maxValues) throws NimbitsException {

        return RecordedValueTransactionFactory.getInstance(point).getTopDataSeries(maxValues);

    }

    @Override
    public List<Value> getDataSegment(final Entity point, final Timespan timespan, final int start, final int end) throws NimbitsException {
        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public List<Value> getDataSegment(final Entity point, final Timespan timespan) throws NimbitsException {
        return RecordedValueTransactionFactory.getInstance(point).getDataSegment(timespan);
    }

    //RPC
    @Override
    public Value recordValue(final User u,
                             final EntityName pointName,
                             final Value value) throws NimbitsException {


        final Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName, PointEntity.class.getName());
      //  final Point point = PointServiceFactory.getInstance().getPointByKey(e.getKey());

        return (e != null) ? recordValue(u, e, value, false) : null;

    }



    @Override
    public Value getPrevValue(final Entity point,
                              final Date timestamp) throws NimbitsException {


        return RecordedValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(timestamp);


    }


    @Override
    public Value getCurrentValue(final Entity p) throws NimbitsException {


        if (p != null) {

            final Value v = getPrevValue(p, new Date());
            if (v != null) {
                final AlertType alertType = getAlertType((Point) p, v);
                return ValueModelFactory.createValueModel(v, alertType);

            }
            else {
                return null;
            }

        }
        else {
            return null;
        }


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
                    return v.getDoubleValue() <= pv.getDoubleValue() + point.getFilterValue()
                            && v.getDoubleValue() >= pv.getDoubleValue() - point.getFilterValue()
                            && v.getNote().equals(pv.getNote())
                            && v.getLatitude() == pv.getLatitude()
                            && v.getLongitude() == pv.getLongitude()
                            && v.getData().equals(pv.getData());

                case percentageHysteresis:
                    if (point.getFilterValue() > 0) {
                        final double p = pv.getDoubleValue() * point.getFilterValue() /100;
                        return v.getDoubleValue() <= pv.getDoubleValue() + p
                                && v.getDoubleValue() >= pv.getDoubleValue() - p;


                    }
                    else {

                        return false;
                    }

                case ceiling:
                    return v.getDoubleValue() >= point.getFilterValue();

                case floor:
                    return v.getDoubleValue() <= point.getFilterValue();

                case none:
                    return false;
                default:
                    return false;
            }



        }


    }

    @Override
    public Value recordValue(final User u,
                             final Entity entity,
                             final Value value,
                             final boolean loopFlag) throws NimbitsException {


        //	RecordedValue prevValue = null;
        Value retObj = null;

        final boolean ignored = false;
        final Point point  = entity instanceof PointModel
                ? (Point) entity
                : (Point) EntityTransactionFactory.getInstance(u).getEntityByKey(entity.getKey(),PointEntity.class);




        final boolean ignoredByDate = ignoreDataByExpirationDate( point, value, ignored);
      //  final boolean ignoredByOwnership = ignoreDataByOwnership(u, point, ignored);
        final boolean ignoredByCompression = ignoreByCompression(  point, value);

        if (!ignoredByDate &&  !ignoredByCompression) {

            retObj = RecordedValueTransactionFactory.getInstance(point).recordValue(value);
            final AlertType t = getAlertType(point, retObj);
            final Value v = ValueModelFactory.createValueModel(retObj, t);
            TaskFactory.getInstance().startRecordValueTask(u, point, v, loopFlag);
        }


        return retObj == null ? value : retObj;

    }

//    private static boolean ignoreDataByOwnership(final User u, final Point point, boolean ignored) {
//        //extra safety check to make sure user isn't writing to someone else's point
//        if (u.getId() != point.getUserFK()) {
//            ignored = true;
//        }
//        return ignored;
//    }

    private static boolean ignoreDataByExpirationDate(final Point p, final Value value, final boolean ignored) {
        boolean retVal = ignored;

        if ( p.getExpire() > 0) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, p.getExpire() * -1);
            if (value.getTimestamp().getTime() < c.getTimeInMillis()) {
                retVal = true;
            }
        }
        return retVal;
    }


    @Override
    public Date getLastRecordedDate(final List<Point> points) throws NimbitsException {
        Date retVal = null;
        Value rx;

        for (final Point p : points) {
            final List<Value> r = RecordedValueTransactionFactory.getInstance(p).getTopDataSeries(1);

            if (!r.isEmpty()) {
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
