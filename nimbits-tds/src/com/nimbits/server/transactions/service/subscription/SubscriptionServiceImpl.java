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

import com.google.appengine.api.datastore.DatastoreTimeoutException;
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
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.facebook.FacebookService;
import com.nimbits.client.service.feed.Feed;
import com.nimbits.client.service.subscription.SubscriptionService;
import com.nimbits.client.service.twitter.TwitterService;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.client.service.xmpp.XMPPService;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.communication.email.EmailService;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.transactions.service.counter.CounterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class SubscriptionServiceImpl extends RemoteServiceServlet implements
        SubscriptionService {
    private static final Logger log = Logger.getLogger(SubscriptionServiceImpl.class.getName());

    private static final int INT = 120;
    private static final int INT1 = 512;
    public static final String LOGO = "http://www.nimbits.com/images/nimbits_transparent_logo.png";
    private static final String CLOUD_URL = "http://cloud.nimbits.com";
    private UserService userService;
    private ValueService valueService;
    private EntityService entityService;
    private EmailService emailService;
    private Feed feedService;
    private TwitterService twitterService;
    private FacebookService facebookService;
    private XMPPService xmppService;
    private CounterService counterService;

    @Override
    public boolean okToProcess(Subscription subscription) {

        boolean retVal;
        try {
            retVal = (counterService.getDateCounter(subscription.getKey()).getTime() +  subscription.getMaxRepeat() *  1000 < new Date().getTime());
        } catch (Exception ex) {
            LogHelper.logException(this.getClass(), ex);
            retVal = true;
        }
        return retVal;

    }


    @Override
    public void processSubscriptions(final User user, final Point point, final Value v) throws NimbitsException {

        final List<Entity> subscriptions= entityService.getSubscriptionsToEntity(user, point);
        log.info("processing " + subscriptions.size() + " subscriptions");
        for (final Entity entity : subscriptions) {

            Subscription subscription = (Subscription) entity;

            if  (okToProcess(subscription)) {

                log.info("Processing Subscription " + subscription.getName().getValue());
                try {
                    counterService.updateDateCounter(subscription.getKey());
                } catch (DatastoreTimeoutException e) {
                   return;// counterService.createShards(subscription.getKey());
                }

                //entityService.addUpdateEntity(user, subscription);

                final List<Entity> subscriptionEntity = entityService.getEntityByKey(user, subscription.getKey(), EntityType.subscription);

                if (! subscriptionEntity.isEmpty())  {

                    final User subscriber =userService.getUserByKey(subscriptionEntity.get(0).getOwner(), AuthLevel.readWriteAll);
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
                            if (valueService.calculateDelta(point) > point.getDeltaAlarm()) {
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
                        counterService.getDateCounter(subscription.getKey()).getTime() + (subscription.getMaxRepeat() *  1000)
                        + " <  " + new Date().getTime());

            }




        }

    }

    private void processSubscriptionToIncreaseOrDecrease(
            final Point point,
            final Value v,
            final Subscription subscription,
            final User subscriber) throws NimbitsException {
        List<Value> prevValue = valueService.getPrevValue(point, new Date(v.getTimestamp().getTime() - 60000));
        if (! prevValue.isEmpty()) {
            if (subscription.getSubscriptionType().equals(SubscriptionType.decrease) && (prevValue.get(0).getDoubleValue() > v.getDoubleValue())) {
                sendNotification(subscriber, subscription, point, v);

            }
            else if (subscription.getSubscriptionType().equals(SubscriptionType.increase) && (prevValue.get(0).getDoubleValue() < v.getDoubleValue())) {
                sendNotification(subscriber, subscription, point, v);
            }
        }
        else {
            sendNotification(subscriber, subscription, point, v);
        }
    }


    private void sendNotification(
            final User user,
            final Subscription subscription,
            final Point point,
            final Value value) throws NimbitsException {

        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                emailService.sendAlert(point, point, user.getEmail(), value, subscription);
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
                feedService.postToFeed(user, point, point, value, FeedType.data);
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

    private void doXMPP(final User u, final Subscription subscription, final Entity entity, final Point point, final Value v) throws NimbitsException {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        final List<XmppResource> resources =  xmppService.getPointXmppResources(u, point);
        if (resources.isEmpty()) {
            xmppService.sendMessage(message, u.getEmail());
        } else {
            log.info("Sending XMPP with resources count: " + resources.size());
            xmppService.sendMessage(u, resources, message, u.getEmail());
        }

    }

    private void sendTweet(final User u, final Entity entity, final Value v) throws NimbitsException {
        final StringBuilder message = new StringBuilder(INT);
        message.append('#').append(entity.getName().getValue()).append(' ');
        message.append("Value=").append(v.getDoubleValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(' ').append(v.getNote());
        }
        message.append(" via #Nimbits");
       twitterService.sendTweet(u, message.toString());
    }

    private void postToFB(final Entity p, final Entity entity, final User u, final Value v) throws NimbitsException {

        String m = entity.getName().getValue() + " = " + v.getDoubleValue();
        if (v.getNote() != null) {
            m += ' ' + v.getNote();
        }

        final StringBuilder picture = new StringBuilder(INT1);



        if (entity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

            final List<Value> values = valueService.getTopDataSeries(p, 10);
            log.info("values for fb chart:" + values.size());
            if (values.isEmpty()) {
                picture.append(LOGO);
            } else {

                picture.append("http://chart.apis.google.com/chart?chd=t:");
                for (int x = values.size(); x > 0; x--) {
                    Value vx = values.get(x-1);
                    picture.append(vx.getDoubleValue()).append(',');
                }

                picture.deleteCharAt(picture.length() - 1);
                picture.append("&chs=100x100&cht=ls&chco=3072F3&chds=0,105&chdlp=b&chls=2,4,1&chma=5,5,5,25&chds=a");
            }



        } else {
            picture.append(LOGO);
        }

        final String link = CLOUD_URL + "?uuid=" + p.getUUID() + "&email=" + p.getOwner();

        final String d = Utils.isEmptyString(entity.getDescription()) ? "" : entity.getDescription();
        log.info(picture.toString());
        log.info(link);

       facebookService.updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
               "nimbits.com", d);


    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setFeedService(Feed feedService) {
        this.feedService = feedService;
    }

    public Feed getFeedService() {
        return feedService;
    }

    public void setTwitterService(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    public TwitterService getTwitterService() {
        return twitterService;
    }

    public void setFacebookService(FacebookService facebookService) {
        this.facebookService = facebookService;
    }

    public FacebookService getFacebookService() {
        return facebookService;
    }

    public void setXmppService(XMPPService xmppService) {
        this.xmppService = xmppService;
    }

    public XMPPService getXmppService() {
        return xmppService;
    }

    public void setCounterService(CounterService counterService) {
        this.counterService = counterService;
    }

    public CounterService getCounterService() {
        return counterService;
    }
}
