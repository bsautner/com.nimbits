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
import com.google.common.collect.Range;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.server.communication.mail.EmailService;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.ValueDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;


@Component
public class SubscriptionService  {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class.getName());

    @Autowired
    private EmailService emailService;


    @Autowired
    private EntityDao entityDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ValueDao valueDao;


    public SubscriptionService() {


    }

    public void process(final User user, final Point point, final Value v) {

        final List<Entity> subscriptions = entityDao.getSubscriptionsToEntity(user, point);
        logger.info("subscription service processing " + subscriptions.size());
        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;


            logger.info("Processing Subscription " + subscription.getName().getValue());
            //  nimbitsCache.put(LAST_SENT_CACHE_KEY_PREFIX + subscription.getId(), new Date());


            //EntityServiceImpl.addUpdateSingleEntity(user, subscription);

            final Entity subscriptionEntity = entityDao.getEntity(user, subscription.getId(),
                    EntityType.subscription).get();


            final User subscriber = userService.getUserByKey(subscriptionEntity.getOwner()).get();
            final AlertType alert = v.getAlertState();

            switch (subscription.getSubscriptionType()) {
                case none:
                    break;
                case anyAlert:
                    if (!alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn() || point.isIdleAlarmOn())) {
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

                case deltaAlert:
                    if (calculateDelta(point) > point.getDeltaAlarm()) {
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

    private void processSubscriptionToIncreaseOrDecrease(


            final Point point,
            final Value v,
            final Subscription subscription,
            final User subscriber
    ) {
        Value prevValue = valueDao.getSnapshot(point);

        if (subscription.getSubscriptionType().equals(SubscriptionType.decrease) && (prevValue.getDoubleValue() > v.getDoubleValue())) {
            sendNotification(subscriber, subscription, point, v);

        } else if (subscription.getSubscriptionType().equals(SubscriptionType.increase) && (prevValue.getDoubleValue() < v.getDoubleValue())) {
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
                EmailAddress emailAddress;
                if (StringUtils.isEmpty(subscription.getTarget())) {
                    emailAddress = user.getEmail();
                } else {
                    emailAddress = CommonFactory.createEmailAddress(subscription.getTarget());
                }
                emailService.sendAlert(point, point, emailAddress, value, subscription);
                break;


            case webhook:

                doWebHook(user, point, value, subscription);
                break;


        }
    }


    private void doWebHook(User user, Point point, Value value, Subscription subscription) {
        WebHook webHook = (WebHook) entityDao.getEntity(user, subscription.getTarget(), EntityType.webhook).get();
        switch (webHook.getMethod()) {

            case POST:
                doPost(webHook, point, value);
                break;
            case GET:
                doGet(user, webHook, point, value);
                break;
            case DELETE:
                break;
            case PUT:
                break;
        }

    }


    private void doPost(WebHook webHook, Point point, Value value) {

        try {
            String message = buildPostBody(webHook, point, value);

            URL url = buildPath(webHook, point, value);

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
                logger.error("error sending user info to nimbits.com: "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage());
            }

        } catch (Exception e) {
            logger.error("error sending user info to nimbits.com", e);
        }
    }

    private URL buildPath(WebHook webHook, Point point, Value value) throws MalformedURLException {
        URL url = null;
        String base = webHook.getUrl().getUrl();

        switch (webHook.getPathChannel()) {

            case none:
                url = new URL(base);
                break;
            case number:
                url = new URL(base + value.getDoubleValue());
                break;
            case data:
                url = new URL(base + value.getData());
                break;
            case meta:
                url = new URL(base + value.getMetaData());
                break;
            case timestamp:
                url = new URL(base + value.getLTimestamp());
                break;
            case gps:
                url = new URL(base + value.getLatitude() + "," + value.getLongitude());
                break;
            case object:
                String json = GsonFactory.getInstance(true).toJson(value);
                url = new URL(base + json);
                break;
        }
        return url;
    }

    private String buildPostBody(WebHook webHook, Point point, Value value) {
        String message = "";
        switch (webHook.getBodyChannel()) {

            case none:
                break;
            case number:
                message = String.valueOf(value.getDoubleValue());
                break;
            case data:
                message = value.getData();
                break;
            case meta:
                message = value.getMetaData();
                break;
            case timestamp:
                message = String.valueOf(value.getLTimestamp());
                break;
            case gps:
                message = value.getLatitude() + "," + value.getLatitude();
                break;
            case object:
                message = GsonFactory.getInstance(true).toJson(value);
                break;
        }
        return message;
    }

    private void doGet(final User user, WebHook webHook, Point point, Value value) {


        InputStream in = null;

        try {
            logger.info("executing webhook GET:" + webHook.getUrl().getUrl());
            URL url = buildPath(webHook, point, value);
            in = url.openStream();
            String result = (IOUtils.toString(in));
            if (!StringUtils.isEmpty(webHook.getDownloadTarget()) && !StringUtils.isEmpty(result)) {
                Optional<Entity> optional = entityDao.getEntity(user, webHook.getDownloadTarget(), EntityType.point);

                if (optional.isPresent()) {
                    Value r = new Value.Builder().data(result).create();
                    valueDao.storeValues(optional.get(), Collections.singletonList(r));
                }


            }
        } catch (IOException e) {
            logger.error("error with subscription", e);
        } finally {
            IOUtils.closeQuietly(in);
        }


    }



    public double calculateDelta(final Point point) {
        double retVal;

        Calendar compareTime = Calendar.getInstance();
        compareTime.add(Calendar.SECOND, (point.getDeltaSeconds() * -1));
        Range<Long> timespan = Range.closed(compareTime.getTime().getTime(), System.currentTimeMillis());
        List<Value> series = valueDao.getSeries(point, Optional.of(timespan), Optional.<Range<Integer>>absent(), Optional.<String>absent());

        //Value start = getCurrentValue(blobStore, point);
        double startValue = series.get(series.size() - 1).getDoubleValue();
        double endValue = series.get(0).getDoubleValue();

        retVal = abs(startValue - endValue);


        return retVal;
    }


}
