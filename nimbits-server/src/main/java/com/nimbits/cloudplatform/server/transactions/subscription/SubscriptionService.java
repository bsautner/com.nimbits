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

package com.nimbits.cloudplatform.server.transactions.subscription;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.ApiProxy;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.constants.Path;
import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.subscription.SubscriptionType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.mqtt.Mqtt;
import com.nimbits.cloudplatform.client.model.mqtt.MqttFactory;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.subscription.Subscription;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.server.communication.email.EmailService;
import com.nimbits.cloudplatform.server.communication.xmpp.XmppServiceImpl;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.http.HttpCommonFactory;
import com.nimbits.cloudplatform.server.transactions.counter.CounterService;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:51 PM
 */
@Service("subscriptionService")

public class SubscriptionService extends RemoteServiceServlet {
    private static final Logger log = Logger.getLogger(SubscriptionService.class.getName());

    private static final int INT = 120;
    private static final int INT1 = 512;
    public static final String LOGO = "http://www.nimbits.com/images/nimbits_transparent_logo.png";
    private static final String CLOUD_URL = "http://cloud.nimbits.com";


    public static boolean okToProcess(Subscription subscription) {

        boolean retVal;
        try {
            retVal = (CounterService.getDateCounter(subscription.getKey()).getTime() + subscription.getMaxRepeat() * 1000 < new Date().getTime());
        } catch (Exception ex) {

            retVal = true;
        }
        return retVal;

    }


    public static void processSubscriptions(final User user, final Point point, final Value v)  {

        final List<Entity> subscriptions = EntityServiceImpl.getSubscriptionsToEntity(user, point);
        log.info("processing " + subscriptions.size() + " subscriptions");
        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;

            if (okToProcess(subscription)) {

                log.info("Processing Subscription " + subscription.getName().getValue());
                try {
                    CounterService.updateDateCounter(subscription.getKey());
                } catch (DatastoreTimeoutException e) {
                    return;// counterService.createShards(subscription.getKey());
                }

                //EntityServiceImpl.addUpdateSingleEntity(user, subscription);

                final List<Entity> subscriptionEntity = EntityServiceImpl.getEntityByKey(user, subscription.getKey(), EntityType.subscription);

                if (!subscriptionEntity.isEmpty()) {

                    final List<User> subscriberList = UserTransaction.getUserByKey(subscriptionEntity.get(0).getOwner(), AuthLevel.readWriteAll);
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
                                if (ValueTransaction.calculateDelta(point) > point.getDeltaAlarm()) {
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
                        CounterService.getDateCounter(subscription.getKey()).getTime() + (subscription.getMaxRepeat() * 1000)
                        + " <  " + new Date().getTime());

            }


        }

    }

    private static void processSubscriptionToIncreaseOrDecrease(
            final Point point,
            final Value v,
            final Subscription subscription,
            final User subscriber) {
        List<Value> prevValue = ValueTransaction.getPrevValue(point, new Date(v.getTimestamp().getTime() - 60000));
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


    private static void sendNotification(
            final User user,
            final Subscription subscription,
            final Point point,
            final Value value) {

        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailService.sendAlert(point, point, user.getEmail(), value, subscription);
                break;
            case instantMessage:
                doXMPP(user, subscription, point, point, value);
                break;

            case cloud:
                break;
        }
    }

    private static void doMQTT(User user, Subscription subscription, Entity entity, Point point, Value value) {

        String valueJson = GsonFactory.getInstance().toJson(value);

        ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
        String host = String.valueOf(env.getAttributes().get("com.google.appengine.runtime.default_version_hostname"));
        log.info("MQTT appId: " + host);
        Mqtt mqtt = MqttFactory.createMqtt(host, user.getEmail(), entity.getKey(), valueJson);
        String mqttJson = GsonFactory.getInstance().toJson(mqtt);
        String params = "data=" + mqttJson;
        HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_MQTT_LOCATION_URL, params);
    }

    private static void doXMPP(final User u, final Subscription subscription, final Entity entity, final Point point, final Value v)  {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        XmppServiceImpl.sendMessage(message, u.getEmail());


    }






}
