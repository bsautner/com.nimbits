/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.subscription;


import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.io.Proximity.ProximityHelper;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.server.communication.mail.EmailService;
import com.nimbits.server.communication.xmpp.XmppService;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.socket.ConnectedClients;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.util.TextUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SubscriptionServiceImpl implements SubscriptionService {
    private static final Logger logger = Logger.getLogger(SubscriptionServiceImpl.class.getName());

    public static final String UTF_8 = "UTF-8";

    private EmailService emailService;

    private ValueTask valueTask;

    private XmppService xmppService;

    private ConnectedClients connectedClients;

    private EntityService entityService;

    private SettingsService settingsService;

    private GeoSpatialDao geoSpatialDao;




    public SubscriptionServiceImpl(GeoSpatialDao geoSpatialDao, EmailService emailService, ValueTask valueTask,
                                   XmppService xmppService, ConnectedClients connectedClients, EntityService entityService, SettingsService settingsService
    ) {
        this.emailService = emailService;
        this.valueTask = valueTask;
        this.xmppService = xmppService;
        this.connectedClients = connectedClients;

        this.entityService = entityService;
        this.settingsService = settingsService;
        this.geoSpatialDao = geoSpatialDao;



    }




    @Override
    public void process(final GeoSpatialDao geoSpatialDao,
                        final TaskService taskService,
                        final UserService userService,
                        final EntityDao entityDao,
                        final ValueTask valueTask,
                        final EntityService entityService,
                        final BlobStore blobStore,
                        final ValueService valueService,
                        final SummaryService summaryService,
                        final SyncService syncService,
                        final SubscriptionService subscriptionService,
                        final CalculationService calculationService,
                        final DataProcessor dataProcessor,
                        final User user, final Point point, final Value v) {

        final List<Entity> subscriptions = entityDao.getSubscriptionsToEntity(user, point);
        logger.info("subscription service processing " + subscriptions.size());
        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;



            logger.info("Processing Subscription " + subscription.getName().getValue());
          //  nimbitsCache.put(LAST_SENT_CACHE_KEY_PREFIX + subscription.getKey(), new Date());


            //EntityServiceImpl.addUpdateSingleEntity(user, subscription);

            final Entity subscriptionEntity = entityDao.getEntityByKey(user, subscription.getKey(),
                    EntityType.subscription).get();


            final User subscriber = userService.getUserByKey(subscriptionEntity.getOwner()).get();
            final AlertType alert = v.getAlertState();

            switch (subscription.getSubscriptionType()) {
                case none:
                    break;
                case anyAlert:
                    if (!alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                        sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor, subscriber, subscription, point, v);
                    }
                    break;
                case high:
                    if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn()) {
                        sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor,subscriber, subscription, point, v);
                    }
                    break;
                case low:
                    if (alert.equals(AlertType.LowAlert) && point.isLowAlarmOn()) {
                        sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor,subscriber, subscription, point, v);
                    }
                    break;
                case idle:
                    if (alert.equals(AlertType.IdleAlert) && point.isIdleAlarmOn()) {
                        sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor,subscriber, subscription, point, v);
                    }
                    break;
                case newValue:
                    sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor,subscriber, subscription, point, v);
                    break;

                case deltaAlert:
                    if (valueService.calculateDelta(blobStore, point) > point.getDeltaAlarm()) {
                        sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor,subscriber, subscription, point, v);
                    }
                    break;
                case increase:
                case decrease:
                    processSubscriptionToIncreaseOrDecrease(geoSpatialDao, taskService, userService, blobStore, entityDao, point, v, subscription, subscriptionService, dataProcessor, subscriber, valueService, calculationService, summaryService, syncService);
                    break;

            }




        }

    }

    private void processSubscriptionToIncreaseOrDecrease(
            final GeoSpatialDao geoSpatialDao,
            final TaskService taskService,
            final UserService userService,
            BlobStore blobStore,
            EntityDao entityDao,
            final Point point,
            final Value v,
            final Subscription subscription,SubscriptionService subscriptionService, DataProcessor dataProcessor,
            final User subscriber,   ValueService valueService,
            CalculationService calculationService,
            SummaryService summaryService, SyncService syncService) {
        Value prevValue = valueService.getCurrentValue(blobStore, point);

        if (subscription.getSubscriptionType().equals(SubscriptionType.decrease) && (prevValue.getDoubleValue() > v.getDoubleValue())) {
            sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor, subscriber, subscription, point, v);

        } else if (subscription.getSubscriptionType().equals(SubscriptionType.increase) && (prevValue.getDoubleValue() < v.getDoubleValue())) {
            sendNotification(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor, subscriber, subscription, point, v);
        }
    }

    //todo this looks like it can be moved out to a new class that excends base processor
    private void sendNotification(
            final GeoSpatialDao geoSpatialDao,
            final TaskService taskService,
            final UserService userService,
            final BlobStore blobStore,
            final EntityDao entityDao,
            final ValueService valueService,
            final CalculationService calculationService,
            final SummaryService summaryService,
            final SyncService syncService,
            final SubscriptionService subscriptionService,
            final DataProcessor dataProcessor,
            final User user,
            final Subscription subscription,
            final Point point,
            final Value value) {

        logger.info("sendNotification: " + subscription.getNotifyMethod());
        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailAddress emailAddress;
                if (StringUtils.isEmpty(subscription.getTarget())) {
                    emailAddress = user.getEmail();
                } else {
                    emailAddress = CommonFactory.createEmailAddress(subscription.getTarget());
                }
                emailService.sendAlert(point, point, emailAddress, value, subscription);
                break;
            case instantMessage:
                doXMPP(user, subscription, point, point, value);
                break;
            case cloud:
                point.setValue(value);
                point.setAction(Action.notify);
                sendGCM(user, subscription, point, Action.notify);
                break;
            case socket:
                point.setAction(Action.notify);
                point.setValue(value);
                sendSocket(entityDao, user, Collections.singletonList(point));
                break;
            case webhook:
                point.setValue(value);
                doWebHook(blobStore, entityDao, valueService, user, point, subscription);
                break;
            case proximity:
                point.setValue(value);
                doProximity(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor,
                        user, point, subscription);
                break;


        }
    }


    //TODO another extract to a class that extends base process
    private void doProximity(final GeoSpatialDao geoSpatialDao, final TaskService taskService,UserService userService, BlobStore blobStore, EntityDao entityDao, ValueService valueService,
                             CalculationService calculationService,
                             SummaryService summaryService, SyncService syncService, SubscriptionService subscriptionService, DataProcessor dataProcessor,
                             User user, Point point, Subscription subscription) {


        try {
            notifyNearbyPoints(geoSpatialDao, taskService, userService, blobStore, entityDao, valueService, calculationService, summaryService, syncService, subscriptionService, dataProcessor , user, point, subscription);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error doing proxitmity", e);

        }


    }



    private void notifyNearbyPoints(final GeoSpatialDao geoSpatialDao,
                                    final TaskService taskService,
                                    final UserService userService,
                                    final BlobStore blobStore,
                                    final EntityDao entityDao,
                                    final ValueService valueService,
                                    final CalculationService calculationService,
                                    final SummaryService summaryService,
                                    final SyncService syncService,
                                    final SubscriptionService subscriptionService,
                                    final DataProcessor dataProcessor,

                                    final User user, final Point point, final Subscription subscription) throws IOException {



        Value value = point.getValue();
        List<Point> allPoints = geoSpatialDao.getNearby(user, value.getLatitude(), value.getLongitude(), value.getDoubleValue());
        logger.info("proximity found nearby: " + allPoints.size());
        logger.info("proximity found lat: " +  value.getLatitude());
        logger.info("proximity found lng: " + value.getLongitude());
        logger.info("proximity found distance: " + value.getDoubleValue());
        for (Point locationPoint : allPoints) {
            Value v = valueService.getCurrentValue(blobStore, locationPoint);

            if (!TextUtils.isEmpty(v.getMetaData())) {
                String m[] = v.getMetaData().split(",");

                String outgoingFeedName = m[0];


                logger.info("proximity found outgoing feed: " + outgoingFeedName);

                Optional<Entity> entityOptional = entityDao.getEntityByName(user,
                        CommonFactory.createName(outgoingFeedName, EntityType.point), EntityType.point);
                if (entityOptional.isPresent()) {
                    try {
                        Point outgoingFeed = (Point) entityOptional.get();
                        logger.info("restarting task service for nearby point: " + outgoingFeedName);
                        taskService.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore, valueService, summaryService, syncService, subscriptionService,
                                calculationService, dataProcessor, user, outgoingFeed, point.getValue());
                        //  point.getValue(), user, outgoingFeed);
                        logger.info("sending data to outgoing feed " + outgoingFeed.getName().getValue());
                    } catch (ValueException e) {
                        logger.severe(e.getMessage());
                    }

                } else {
                    logger.severe("attempt to notify a nearby point in a subscription failed because it wasn't found: outgoingFeedName=" + outgoingFeedName);
                }
            }
            else {
                logger.severe("attempt to notify a nearby point in a subscription failed because meta data with outgoing feed is missing");
            }
        }

    }

    private void doWebHook(BlobStore blobStore, EntityDao entityDao, ValueService valueService, User user, Point point, Subscription subscription) {
        WebHook webHook = (WebHook) entityDao.getEntityByKey(user, subscription.getTarget(), EntityType.webhook).get();
        switch (webHook.getMethod()) {

            case POST:
                doPost(webHook, point);
                break;
            case GET:
                doGet(blobStore, entityDao, valueService, user, webHook, point);
                break;
            case DELETE:
                break;
            case PUT:
                break;
        }

    }


    private void doPost(WebHook webHook, Point point) {

        try {
            String message = buildPostBody(webHook, point);

            URL url = buildPath(webHook, point);

            logger.info("executing webhook POST: " + webHook.getUrl().getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(message);
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                logger.info("sent user to nimbits.com");
            } else {
                logger.log(Level.SEVERE, "error sending user info to nimbits.com: "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage());
            }

        } catch ( Exception e) {
            logger.log(Level.SEVERE, "error sending user info to nimbits.com", e);
        }
    }

    private URL buildPath(WebHook webHook, Point point) throws MalformedURLException {
        URL url = null;
        String base = webHook.getUrl().getUrl();

        switch (webHook.getPathChannel()) {

            case none:
                url = new URL(base);
                break;
            case number:
                url = new URL(base + point.getValue().getDoubleValue());
                break;
            case data:
                url = new URL(base + point.getValue().getData());
                break;
            case meta:
                url = new URL(base + point.getValue().getMetaData());
                break;
            case timestamp:
                url = new URL(base+ point.getValue().getTimestamp());
                break;
            case gps:
                url = new URL(base + point.getValue().getLatitude() + "," + point.getValue().getLongitude());
                break;
            case object:
                String json =  GsonFactory.getInstance(true).toJson(point.getValue());
                url = new URL(base + json);
                break;
        }
        return url;
    }

    private String buildPostBody(WebHook webHook, Point point) {
        String message = "";
        switch (webHook.getBodyChannel()) {

            case none:
                break;
            case number:
                message = String.valueOf(point.getValue().getDoubleValue());
                break;
            case data:
                message = point.getValue().getData();
                break;
            case meta:
                message = point.getValue().getMetaData();
                break;
            case timestamp:
                message = String.valueOf(point.getValue().getTimestamp());
                break;
            case gps:
                message = point.getValue().getLatitude() + "," + point.getValue().getLatitude();
                break;
            case object:
                message =  GsonFactory.getInstance(true).toJson(point.getValue());
                break;
        }
        return message;
    }

    private void doGet(BlobStore blobStore, EntityDao entityDao, ValueService valueService, final User user, WebHook webHook, Point point) {


        InputStream in = null;

        try {
            logger.info("executing webhook GET:"  + webHook.getUrl().getUrl());
            URL url = buildPath(webHook, point);
            in = url.openStream();
            String result = ( IOUtils.toString(in) );
            if (! StringUtils.isEmpty(webHook.getDownloadTarget()) && ! StringUtils.isEmpty(result)) {
                Point target = (Point) entityDao.getEntityByKey(user, webHook.getDownloadTarget(), EntityType.point).get();

                Value value = new Value.Builder().data(result).create();
                valueService.recordValues(blobStore, user, target, Collections.singletonList(value));


            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error with subscription", e);
        } finally {
            IOUtils.closeQuietly(in);
        }


    }



    private void doXMPP(final User u, final Subscription subscription, final Entity entity, final Point point, final Value v) {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance(true).toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        xmppService.sendMessage(message, u.getEmail());


    }

    class Payload {
        @Expose
        String to;
        @Expose
        Value data;


        public Payload(String to, Value data) {
            this.to = to;
            this.data = data;
        }
    }
    @Override
    public void sendGCM(final User user, Subscription subscription, Point point, Action action) {

        try {

            String API_KEY = settingsService.getSetting(ServerSetting.gcm);
            Gson gson =  GsonFactory.getInstance(true);
            Value v = new Value.Builder().initValue(point.getValue()).meta(point.getKey()).create();
            Payload payload = new Payload(subscription.getTarget(), v);
            String json = gson.toJson(payload);
            logger.info("GCM: " + json);
            logger.info(API_KEY);

            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(json.getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            logger.info(resp);

        } catch (Exception e) {
            logger.severe("Unable to send GCM message." + e.getMessage());

        }

    }

    @Override
    public List<Entity> sendSocket(EntityDao entityDao, User user, List<Point> points) {

        try {
            connectedClients.sendMessage(user, points);

            List<Entity> outbound = entityService.getEntitiesByType(user, EntityType.socket);
            for (Entity entity : outbound) {
                Socket socket = (Socket) entity;
                connectedClients.sendOutbound(entityDao, this, user, socket, points);

            }
            return outbound;

        } catch (IOException e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processIncomingSocketValues(EntityDao entityDao, User user, Point point) {
        Point sample = (Point) entityDao.getEntityByKey(user, point.getKey(), EntityType.point).get();
        //TODO taskService.startRecordValueTask(calculationService, user,  sample, point.getValue(), true);

    }

    private String getQuery(final List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), UTF_8));
        }

        return result.toString();
    }


}
