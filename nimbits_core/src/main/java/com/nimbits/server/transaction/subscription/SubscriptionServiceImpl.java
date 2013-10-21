/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transaction.subscription;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.communication.email.EmailServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.counter.CounterServiceFactory;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class SubscriptionServiceImpl implements SubscriptionService {
    private static final Logger log = Logger.getLogger(SubscriptionServiceImpl.class.getName());
    public static final String NIMBITS_GCM_URL = "http://nimbits-gcm.appspot.com/sendAll";
    public static final String METHOD = "POST";
    public static final String UTF_8 = "UTF-8";
    private final EntityService entityService;
    private final ValueService valueService;

    private final NimbitsEngine engine;

    public SubscriptionServiceImpl(NimbitsEngine engine, TaskService taskService) {
        this.engine = engine;
        this.entityService = EntityServiceFactory.getInstance(engine);
        this.valueService = ValueServiceFactory.getInstance(engine, taskService);
    }

    public static boolean okToProcess(Subscription subscription) {

        boolean retVal;
        try {
            retVal = (CounterServiceFactory.getInstance().getDateCounter(subscription.getKey()).getTime() + subscription.getMaxRepeat() * 1000 < new Date().getTime());
        } catch (Exception ex) {

            retVal = true;
        }
        return retVal;

    }


    @Override
    public void processSubscriptions(final User user, final Point point, final Value v)  {

        final List<Entity> subscriptions =entityService.getSubscriptionsToEntity(user, point);
        log.info("processing " + subscriptions.size() + " subscriptions");
        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;

            if (okToProcess(subscription)) {

                log.info("Processing Subscription " + subscription.getName().getValue());
                try {
                    CounterServiceFactory.getInstance().updateDateCounter(subscription.getKey());
                } catch (DatastoreTimeoutException e) {
                    return;// counterService.createShards(subscription.getKey());
                }

                //EntityServiceImpl.addUpdateSingleEntity(user, subscription);

                final List<Entity> subscriptionEntity = entityService.getEntityByKey(user, subscription.getKey(), EntityType.subscription);

                if (!subscriptionEntity.isEmpty()) {

                    final List<User> subscriberList = AuthenticationServiceFactory.getInstance(engine).getUserByKey(subscriptionEntity.get(0).getOwner(), AuthLevel.readWriteAll);
                    final AlertType alert = v.getAlertState();
                    if (! subscriberList.isEmpty()) {
                        User subscriber = subscriberList.get(0);
                        switch (subscription.getSubscriptionType()) {
                            case none:
                                break;
                            case anyAlert:
                                if (!alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                                    sendNotification(subscriber, subscription, point, v);
                                }
                                break;
                            case high:
                                if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn()) {
                                    sendNotification(subscriber, subscription, point, v);
                                }
                                break;
                            case low:
                                if (alert.equals(AlertType.LowAlert) && point.isLowAlarmOn()) {
                                    sendNotification(subscriber, subscription, point, v);
                                }
                                break;
                            case idle:
                                if (alert.equals(AlertType.IdleAlert) && point.isIdleAlarmOn()) {
                                    sendNotification(subscriber, subscription, point, v);
                                }
                                break;
                            case newValue:
                                sendNotification(subscriber, subscription, point, v);
                                break;
                            case changed:
                                break;
                            case deltaAlert:
                                if (valueService.calculateDelta(point) > point.getDeltaAlarm()) {
                                    sendNotification(subscriber, subscription, point, v);
                                }
                                break;
                            case increase:
                            case decrease:
                                processSubscriptionToIncreaseOrDecrease(point, v, subscription, subscriber);
                                break;

                        }
                    }

                }

            } else {
                log.info("Not running subscription because " +
                        CounterServiceFactory.getInstance().getDateCounter(subscription.getKey()).getTime() + (subscription.getMaxRepeat() * 1000)
                        + " <  " + new Date().getTime());

            }


        }

    }

    private void processSubscriptionToIncreaseOrDecrease(
            final Point point,
            final Value v,
            final Subscription subscription,
            final User subscriber) {
        List<Value> prevValue = valueService.getPrevValue(point, new Date(v.getTimestamp().getTime() - 60000));
        if (!prevValue.isEmpty()) {
            if (subscription.getSubscriptionType().equals(SubscriptionType.decrease) && (prevValue.get(0).getDoubleValue() > v.getDoubleValue())) {
                sendNotification(subscriber, subscription, point, v);

            } else if (subscription.getSubscriptionType().equals(SubscriptionType.increase) && (prevValue.get(0).getDoubleValue() < v.getDoubleValue())) {
                sendNotification(subscriber, subscription, point, v);
            }
        } else {
            sendNotification(subscriber, subscription, point, v);
        }
    }


    private void sendNotification(
            final User user,
            final Subscription subscription,
            final Point point,
            final Value value) {

        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailServiceFactory.getServiceInstance(engine).sendAlert(point, point, user.getEmail(), value, subscription);
                break;
            case instantMessage:
                doXMPP(user, subscription, point, point, value);
                break;
            case cloud:
                doCloud(user, point, value);
                break;
        }
    }



    private void doXMPP(final User u, final Subscription subscription, final Entity entity, final Point point, final Value v)  {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        engine.getXmppService().sendMessage(message, u.getEmail());


    }

    private void doCloud(final User user, final Point point, final Value v)  {


        try {

            URL url = new URL(NIMBITS_GCM_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(METHOD);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            point.setValue(v);
            String json = GsonFactory.getInstance().toJson(point);
            params.add(new BasicNameValuePair(Parameters.email.getText(), user.getEmail().getValue()));
            params.add(new BasicNameValuePair(Parameters.json.getText(), json));
            params.add(new BasicNameValuePair(Parameters.action.getText(), Action.notify.getCode()));
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(getQuery(params));

            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
               log.info("cloud message sent");
            } else {
                log.severe("cloud message failed with " + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
             log.severe(e.getMessage());
        } catch (IOException e) {
            log.severe(e.getMessage());
        }


    }

    private String getQuery(final List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
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
