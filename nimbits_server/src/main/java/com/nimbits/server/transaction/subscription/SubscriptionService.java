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
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

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


    private final EmailService emailService;


    private final EntityDao entityDao;


    private final UserService userService;


    private final ValueDao valueDao;


    private final TaskExecutor taskExecutor;

    @Autowired
    public SubscriptionService(EmailService emailService, EntityDao entityDao,
                               UserService userService, ValueDao valueDao, TaskExecutor taskExecutor) {
        this.emailService = emailService;
        this.entityDao = entityDao;
        this.userService = userService;
        this.valueDao = valueDao;
        this.taskExecutor = taskExecutor;


    }

    public void process(final User user, final Point point, final Value v)   {

        taskExecutor.execute(new SubscriptionRunner(user, point, v));
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

    private class SubscriptionRunner implements Runnable {
        final User user;
        final Point point;
        final Value v;

        SubscriptionRunner(User user, Point point, Value v) {
            this.user = user;
            this.point = point;
            this.v = v;
        }

        public void run()  {


            final List<Entity> subscriptions = entityDao.getSubscriptionsToEntity(user, point);
            logger.info(String.format("subscription service processing point: %s count: %s",  point.getName(), subscriptions.size()));
            for (final Entity entity : subscriptions) {


                Subscription subscription = (Subscription) entity;

                logger.info("Processing Subscription " + subscription.getName().getValue());


                final Optional<Entity> entityOptional = entityDao.getEntity(user, subscription.getId(),
                        EntityType.subscription);

                if (entityOptional.isPresent()) {
                    final Entity subscriptionEntity = entityOptional.get();
                    final Optional<User> subscriberOptional = userService.getUserByKey(subscriptionEntity.getOwner());

                    if (subscriberOptional.isPresent()) {
                        final User subscriber = subscriberOptional.get();
                        final AlertType alert = v.getAlertState();

                        try {
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
                        } catch (Exception e) {
                            logger.error("error processing subscription" , e);
                        }
                    }
                }

            }

        }


        private void processSubscriptionToIncreaseOrDecrease(


                final Point point,
                final Value v,
                final Subscription subscription,
                final User subscriber
        ) throws Exception {
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
                final Value value) throws Exception {


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

                    try {
                        doWebHook(user, value, subscription);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;


            }
        }


        private void doWebHook(User user, Value value, Subscription subscription) throws IOException {
            Optional<Entity> webHookOptional = entityDao.getEntity(user, subscription.getTarget(), EntityType.webhook);

            if (webHookOptional.isPresent()) {
                WebHook webHook = (WebHook) webHookOptional.get();
                switch (webHook.getMethod()) {

                    case POST:
                        doPost(webHook, value);
                        break;
                    case GET:
                        doGet(user, webHook, value);
                        break;
                    case DELETE:
                        break;
                    case PUT:
                        break;
                }
            }

        }


        private void doPost(WebHook webHook, Value value) throws IOException {


            String message = buildPostBody(webHook, value);

            URL url = buildPath(webHook, value);

            logger.info("executing web hook POST: " + webHook.getUrl().getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(message);
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                logger.info("web hook succeeded 200 OK");
            } else {
                logger.error("web hook failed with error. Web hook URL responded with: "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage());
            }


        }

        private URL buildPath(WebHook webHook, Value value) throws MalformedURLException {
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

        private String buildPostBody(WebHook webHook, Value value) {
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

        private void doGet(final User user, WebHook webHook, Value value) throws IOException {


            InputStream in = null;

            try {
                logger.info("executing webhook GET:" + webHook.getUrl().getUrl());
                URL url = buildPath(webHook, value);
                in = url.openStream();
                String result = (IOUtils.toString(in));
                if (!StringUtils.isEmpty(webHook.getDownloadTarget()) && !StringUtils.isEmpty(result)) {
                    Optional<Entity> optional = entityDao.getEntity(user, webHook.getDownloadTarget(), EntityType.point);

                    if (optional.isPresent()) {
                        Value r = new Value.Builder().data(result).create();
                        Optional<Entity> targetOptional = entityDao.getEntity(user, webHook.getDownloadTarget(), EntityType.point);
                        if (targetOptional.isPresent()) {
                            // valueTask.process(user, (Point) targetOptional.get(), r );
                            Point target = (Point) targetOptional.get();
                            valueDao.storeValues(target, Collections.singletonList(r));

                            if (target.isIdleAlarmOn() && target.idleAlarmSent()) {
                                entityDao.setIdleAlarmSentFlag(target.getId(), false, false);
//                                target.setIdleAlarmSent(false);
//                                entityDao.addUpdateEntity(user, target);
                                //entityService.addUpdateEntity(user, point);
                            }
                        }
                    }


                }
            } finally {
                IOUtils.closeQuietly(in);
            }


        }



    }




}
