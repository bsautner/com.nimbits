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

package com.nimbits.server.transactions.service.subscription;

import com.google.apphosting.api.ApiProxy;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Path;

import com.nimbits.client.enums.*;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.mqtt.Mqtt;
import com.nimbits.client.model.mqtt.MqttFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.xmpp.XmppResource;
import com.nimbits.client.service.subscription.SubscriptionService;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.communication.email.EmailServiceFactory;
import com.nimbits.server.communication.xmpp.XmppServiceFactory;
import com.nimbits.server.external.facebook.FacebookFactory;
import com.nimbits.server.external.twitter.TwitterServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.feed.FeedServiceFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:51 PM
 */
public class SubscriptionServiceImpl extends RemoteServiceServlet implements
        SubscriptionService {
    private static final Logger log = Logger.getLogger(SubscriptionServiceImpl.class.getName());

    private static final int INT = 120;
    private static final int INT1 = 512;


    public boolean okToProcess(Subscription subscription) {

        boolean retVal;
        try {
            retVal = (subscription.getLastSent().getTime() +  subscription.getMaxRepeat() *  1000 < new Date().getTime());
        } catch (Exception ex) {
            LogHelper.logException(this.getClass(), ex);
            retVal = true;
        }
        return retVal;

    }


    @Override
    public void processSubscriptions(final User user, final Point point, final Value v) throws NimbitsException {

        final List<Entity> subscriptions= EntityServiceFactory.getInstance().getSubscriptionsToEntity(user, point);
        log.info("processing " + subscriptions.size() + " subscriptions");
        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;

            if  (okToProcess(subscription)) {

                log.info("Processing Subscription " + subscription.getKey());
                subscription.setLastSent(new Date());
                EntityServiceFactory.getInstance().addUpdateEntity(user, subscription);

                final List<Entity> subscriptionEntity = EntityServiceFactory.getInstance().getEntityByKey(user, subscription.getKey(), EntityType.subscription);

                if (! subscriptionEntity.isEmpty())  {

                    final User subscriber = UserServiceFactory.getInstance().getUserByKey(subscriptionEntity.get(0).getOwner(), AuthLevel.readWriteAll);
                    final AlertType alert = v.getAlertState();

                    switch (subscription.getSubscriptionType()) {
                        case none:
                            break;
                        case anyAlert:
                            if (!alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                                sendNotification(subscriber,subscription, point, v);
                            }
                            break;
                        case high:
                            if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn()) {
                                sendNotification(subscriber,  subscription, point, v);
                            }
                            break;
                        case low:
                            if (alert.equals(AlertType.LowAlert) && point.isLowAlarmOn()) {
                                sendNotification(subscriber, subscription, point, v);
                            }
                            break;
                        case idle:
                            if (alert.equals(AlertType.IdleAlert) && point.isIdleAlarmOn()) {
                                sendNotification(subscriber,  subscription, point, v);
                            }
                            break;
                        case newValue:
                            sendNotification(subscriber,subscription, point, v);
                            break;
                        case changed:
                            break;
                        case deltaAlert:
                            if (ValueServiceFactory.getInstance().calculateDelta(point) > point.getDeltaAlarm()) {
                                sendNotification(subscriber,subscription, point, v);
                            }
                            break;
                        case increase: case decrease:
                            processSubscriptionToIncreaseOrDecrease(point, v, subscription, subscriber);
                            break;




                    }

                }

            }
            else {
                log.info("Not running subscription because " +
                        subscription.getLastSent().getTime() + subscription.getMaxRepeat() *  1000
                        + " <  " + new Date().getTime());

            }




        }

    }

    private void processSubscriptionToIncreaseOrDecrease(Point point, Value v, Subscription subscription, User subscriber) throws NimbitsException {
        List<Value> prevValue = ValueServiceFactory.getInstance().getPrevValue(point, new Date(v.getTimestamp().getTime() - 1000));
        if (! prevValue.isEmpty()) {
            if (subscription.getSubscriptionType().equals(SubscriptionType.decrease) && (prevValue.get(0).getDoubleValue() > v.getDoubleValue())) {
                sendNotification(subscriber, subscription, point, v);

            }
            else if (subscription.getSubscriptionType().equals(SubscriptionType.increase) && (prevValue.get(0).getDoubleValue() < v.getDoubleValue())) {
                sendNotification(subscriber, subscription, point, v);
            }
        }
    }


    private static void sendNotification(
            final User user,
            final Subscription subscription,
            final Point point,
            final Value value) throws NimbitsException {

        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailServiceFactory.getInstance().sendAlert(point, point, user.getEmail(), value, subscription);
                break;
            case facebook:
                postToFB(point,point, user, value);
                break;
            case twitter:
                sendTweet(user, point, value);
                break;
            case instantMessage:
                doXMPP(user, subscription, point, point, value);
                break;
            case mqtt:
                doMQTT(user, subscription, point, point, value);
                break;
            case feed:
                FeedServiceFactory.getInstance().postToFeed(user, point, point, value, FeedType.data);
                break;
        }
    }

    private static void doMQTT(User user, Subscription subscription, Entity entity, Point point, Value value) throws NimbitsException {

        String valueJson = GsonFactory.getInstance().toJson(value);

        ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
        String host =  String.valueOf(env.getAttributes().get("com.google.appengine.runtime.default_version_hostname"));



        log.info("MQTT appId: " + host);
        Mqtt mqtt = MqttFactory.createMqtt(host, user.getEmail(), entity.getKey(), valueJson);
        String mqttJson = GsonFactory.getInstance().toJson(mqtt);
        String params = "data=" + mqttJson;
        HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_MQTT_LOCATION_URL, params);
    }

    private static void doXMPP(final User u, final Subscription subscription, final Entity entity, final Point point, final Value v) throws NimbitsException {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        final List<XmppResource> resources =  XmppServiceFactory.getInstance().getPointXmppResources(u, point);
        if (resources.isEmpty()) {
            XmppServiceFactory.getInstance().sendMessage(message, u.getEmail());
        } else {
            log.info("Sending XMPP with resources count: " + resources.size());
            XmppServiceFactory.getInstance().sendMessage(resources, message, u.getEmail());
        }

    }

    private static void sendTweet(final User u, final Entity entity, final Value v) throws NimbitsException {
        final StringBuilder message = new StringBuilder(INT);
        message.append('#').append(entity.getName().getValue()).append(' ');
        message.append("Value=").append(v.getDoubleValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(' ').append(v.getNote());
        }
        message.append(" via #Nimbits");
        TwitterServiceFactory.getInstance().sendTweet(u, message.toString());
    }

    private static void postToFB(final Entity p, final Entity entity, final User u, final Value v) throws NimbitsException {

        String m = entity.getName().getValue() + " = " + v.getDoubleValue();
        if (v.getNote() != null) {
            m += ' ' + v.getNote();
        }

        final StringBuilder picture = new StringBuilder(INT1);



        if (entity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

            final List<Value> values = ValueServiceFactory.getInstance().getTopDataSeries(p, 10);
            if (values.isEmpty()) {
                picture.append("http://app.nimbits.com/resources/images/logo.png");
            } else {

                picture.append("http://chart.apis.google.com/chart?chd=t:");
                for (int x = values.size(); x >= 0; x--) {
                    Value vx = values.get(x);
                    picture.append(vx.getDoubleValue()).append(',');
                }

                picture.deleteCharAt(picture.length() - 1);
                picture.append("&chs=100x100&cht=ls&chco=3072F3&chds=0,105&chdlp=b&chls=2,4,1&chma=5,5,5,25&chds=a");
            }



        } else {
            picture.append("http://app.nimbits.com/resources/images/logo.png");
        }

        final String link = "http://app.nimbits.com?uuid=" + p.getUUID() + "&email=" + p.getOwner();

        final String d = Utils.isEmptyString(entity.getDescription()) ? "" : entity.getDescription();
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
                "nimbits.com", d);


    }
}
