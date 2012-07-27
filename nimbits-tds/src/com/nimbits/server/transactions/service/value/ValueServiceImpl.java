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

import com.google.gdata.client.Query;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.api.helper.LocationReportingHelperFactory;
import com.nimbits.server.api.openid.UserInfo;
import com.nimbits.server.process.task.TaskFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;


public class ValueServiceImpl extends RemoteServiceServlet implements
        ValueService, RequestCallback {

    static final Logger log = Logger.getLogger(ValueServiceImpl.class.getName());
    private static final long serialVersionUID = 1L;





    @Override
    public List<Value> getTopDataSeries(final Entity entity,
                                        final int maxValues,
                                        final Date endDate) throws NimbitsException {
//        final Point p = (Point) EntityServiceFactory.getInstance().getEntityByKey(entity.getKey(), PointEntity.class.getName());
        return ValueTransactionFactory.getInstance(entity).getTopDataSeries(maxValues, endDate);
    }


    @Override
    public void preloadTimespan(Entity entity, Timespan timespan) throws NimbitsException {
        ValueTransactionFactory.getInstance(entity).preloadTimespan(timespan);
    }

    @Override
    public List<Value> getCache(final Entity entity) throws NimbitsException {
        //  final Point point = PointServiceFactory.getInstance().getPointByKey(entity.getKey());
        //  final Point p = (Point) EntityServiceFactory.getInstance().getEntityByKey(entity.getKey(), PointEntity.class.getName());
        return ValueTransactionFactory.getInstance(entity).getBuffer();
    }



    @Override
    public List<Value> getPieceOfDataSegment(final Entity entity,
                                             final Timespan timespan,
                                             final int start,
                                             final int end) throws NimbitsException {
        return ValueTransactionFactory.getInstance(entity).getDataSegment(timespan, start, end);
    }

    @Override
    public Value recordValue(final Entity point,
                             final Value value) throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        LocationReportingHelperFactory.getInstance().reportLocation(this.getThreadLocalRequest(), point);
//        final Point px = PointServiceFactory.getInstance().getPointByKey(point.getKey());
        //   final Point px = (Point) EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), PointEntity.class.getName());
        return recordValue(u,point, value);
    }


    @Override
    public List<Value> getTopDataSeries(final Entity point,
                                        final int maxValues) throws NimbitsException {

        return ValueTransactionFactory.getInstance(point).getTopDataSeries(maxValues);

    }

    @Override
    public List<Value> getDataSegment(final Entity point, final Timespan timespan, final int start, final int end) throws NimbitsException {
        return ValueTransactionFactory.getInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public List<Value> getDataSegment(final Entity point, final Timespan timespan) throws NimbitsException {
        return ValueTransactionFactory.getInstance(point).getDataSegment(timespan);
    }

    //RPC
    @Override
    public Value recordValue(final User u,
                             final EntityName pointName,
                             final Value value) throws NimbitsException {


        final List<Entity> e = EntityServiceFactory.getInstance().getEntityByName(u, pointName,EntityType.point);


        return e.isEmpty() ? null : recordValue(u, e.get(0), value);

    }



    @Override
    public Value getPrevValue(final Entity point,
                              final Date timestamp) throws NimbitsException {


        return ValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(timestamp);


    }
    @Override
    public Map<String, Entity> getCurrentValues(final Map<String, Point> entities) throws NimbitsException {
        final Map<String, Entity> retObj = new HashMap<String, Entity>(entities.size());
        for (final Point p : entities.values()) {

            final List<Value> v = ValueServiceFactory.getInstance().getCurrentValue(p);
            if (! v.isEmpty()) {
                p.setValue(v.get(0));

            }
            retObj.put(p.getKey(), p);
        }
        return retObj;

    }

    @Override
    public List<ValueBlobStore> getAllStores(Entity entity) throws NimbitsException {
        return  ValueTransactionFactory.getInstance(entity).getAllStores();
    }

    @Override
    public void purgeValues(Entity entity) throws NimbitsException {
        ValueTransactionFactory.getInstance(entity).purgeValues();

    }

    @Override
    public void deleteExpiredData(Point point) {
        ValueTransactionFactory.getInstance(point).deleteExpiredData();
    }

    @Override
    public void recordValues(User user, Point point, List<Value> values) throws NimbitsException {
        if (point.getOwner().equals(user.getKey())) {
            ValueTransactionFactory.getInstance(point).recordValues(values);
        }
    }

    @Override
    public String startGoogleDocExport(final Entity entity, final int count, final Timespan ts) throws NimbitsException {
        final User user = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        DocsService docsService;
        SpreadsheetService spreadsheetService;// = new SpreadsheetsService("MySpreadsheetIntegration-v1");
        String consumerKey = getInitParameter("consumer_key");
        String consumerSecret = getInitParameter("consumer_secret");
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);
        docsService = new DocsService("nimbits-com");
        spreadsheetService = new SpreadsheetService("nimbits-com");


        try {
            docsService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
            spreadsheetService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());




            SpreadsheetEntry entry = new SpreadsheetEntry();
            String title = entity.getName().getValue();
            entry.setTitle(TextConstruct.plainText(title));
            try {

                if (count == -2) {
                    SpreadsheetEntry newEntry = docsService.insert(
                            new URL("https://docs.google.com/feeds/default/private/full?xoauth_requestor_id="
                                    + user.getEmail()),
                            entry);
                    return "created";

                }
                else if (count == -1) {
                    preloadTimespan(entity, ts);

                    return "loaded";
                }
                else {
                    SpreadsheetQuery query =
                            new SpreadsheetQuery(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"));

                    query.addCustomParameter(new Query.CustomParameter(
                            "xoauth_requestor_id", user.getEmail().getValue()));

                    query.setTitleQuery(title);
                    SpreadsheetFeed feed =  spreadsheetService.query(query, SpreadsheetFeed.class);
                    if (feed != null && ! feed.getEntries().isEmpty()) {
                        List<Value> values = ValueTransactionFactory.getInstance(entity).getPieceOfPreload(count, count + 100);
                        if (values.size() > 0) {

                            com.google.gdata.data.spreadsheet.SpreadsheetEntry wsEntry = feed.getEntries().get(0);


                            WorksheetEntry sheet = wsEntry.getWorksheets().get(0);

                            URL cellFeedUrl= sheet.getCellFeedUrl ();

                            CellFeed cellFeed= spreadsheetService.getFeed (cellFeedUrl,
                                    CellFeed.class);
                            CellEntry cellEntry;

                            if (count == 0) {

                                cellEntry= new CellEntry (1, 1, "Timestamp");
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (1, 2, "Value");
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (1, 3, "Latitude");
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (1, 4, "Longitude");
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (1, 5, "Annotation");
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (1, 6, "Data");
                                cellFeed.insert (cellEntry);

                            }

                            SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");
                            int row = count + 2;
                            for (Value value : values) {
                                cellEntry= new CellEntry (row, 1, dtf.format(value.getTimestamp()));
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (row, 2, String.valueOf(value.getDoubleValue()));
                                cellFeed.insert (cellEntry);


                                cellEntry= new CellEntry (row, 3, String.valueOf(value.getLatitude()));
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (row, 4,String.valueOf(value.getLongitude()));
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (row, 5, value.getNote());
                                cellFeed.insert (cellEntry);

                                cellEntry= new CellEntry (row, 6, value.getData().getContent());
                                cellFeed.insert (cellEntry);
                                row++;

                            }
                            return "found";
                        }
                        else {
                            return "done";
                        }


                    }
                    else {
                        return "error";
                    }


                }






            } catch (ServiceException e) {
                LogHelper.logException(this.getClass(), e);
                throw new NimbitsException(e);
            } catch (MalformedURLException e) {
                throw new NimbitsException(e);
            } catch (IOException e) {
                throw new NimbitsException(e);
            }
        } catch (OAuthException e) {
            throw new NimbitsException(e);
        }

    }




    @Override
    public List<Value> getCurrentValue(final Entity p) throws NimbitsException {


        if (p != null) {
            List<Value> retObj = new ArrayList<Value>(1);
            final Value v = getPrevValue(p, new Date());
            if (v != null) {
                final AlertType alertType = getAlertType((Point) p, v);
                retObj.add(ValueFactory.createValueModel(v, alertType));

            }
            return retObj;
        }
        else {
            return Collections.emptyList();
        }




    }
    private static AlertType getAlertType(final Point point, final Value value)  {
        AlertType retObj = AlertType.OK;

        if (point.isHighAlarmOn() || point.isLowAlarmOn()) {

            if (point.isHighAlarmOn() && value.getDoubleValue() >= point.getHighAlarm()) {
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
    protected boolean ignoreByFilter(final Point point, final Value v) throws NimbitsException {


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

    private static boolean ignoreByAuthLevel(final User u, final Entity entity) throws NimbitsException {
        for (AccessKey k : u.getAccessKeys()) {
            log.info("key: " + k.getCode() + ' '  + k.getScope() + ' '  + entity.getKey());
        }
        if (u.isRestricted()) {
            return true;
        }


        for (final AccessKey key : u.getAccessKeys()) {
            if (key.getAuthLevel().equals(AuthLevel.admin)) {
                return false;
            }
            if (key.getScope().equals(entity.getKey()) || key.getScope().equals(entity.getOwner())) {
                if (key.getAuthLevel().compareTo(AuthLevel.readWritePoint) >= 0) {
                    return false;
                }

            }
        }

        return true;

    }

    @Override
    public Value recordValue(final User u,
                             final Entity entity,
                             final Value value) throws NimbitsException {


        //	RecordedValue prevValue = null;

        final Point point;

        if (! entity.getEntityType().recordsData()) {
            throw new NimbitsException("You can only record data to a Point. Entity Type was: " + entity.getEntityType().getClassName());
        }

        if (entity instanceof Point)  {
            point = (Point) entity;
        }
        else {
            List<Entity> points  =  EntityServiceFactory.getInstance().getEntityByKey(u, entity.getKey(), entity.getEntityType());
            if (! points.isEmpty()) {
                point = (Point) points.get(0);
            }
            else {
                throw new NimbitsException("Point Not Found");
            }
        }

        if (ignoreByAuthLevel(u, entity)) {
            throw new NimbitsException("Could not record value do to permissions levels being to low for a write operation");
        } else {

            final boolean ignored = false;
            final boolean ignoredByDate = ignoreDataByExpirationDate(point, value, ignored);

            final boolean ignoredByCompression = ignoreByFilter(point, value);

            Value retObj = null;
            if (!ignoredByDate && !ignoredByCompression) {

                retObj = ValueTransactionFactory.getInstance(point).recordValue(value);
                final AlertType t = getAlertType(point, retObj);
                final Value v = ValueFactory.createValueModel(retObj, t);
                TaskFactory.getInstance().startRecordValueTask(u, point, v);
            }


            return retObj == null ? value : retObj;
        }
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

        for (final Point p : points) {
            final List<Value> r = ValueTransactionFactory.getInstance(p).getTopDataSeries(1);

            if (!r.isEmpty()) {
                Value rx = r.get(0);
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
