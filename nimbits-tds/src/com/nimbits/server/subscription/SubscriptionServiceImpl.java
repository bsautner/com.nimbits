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

package com.nimbits.server.subscription;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FeedType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.xmpp.XmppResource;
import com.nimbits.client.service.subscription.SubscriptionService;
import com.nimbits.server.email.EmailServiceFactory;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.facebook.FacebookFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.twitter.TwitterServiceFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;
import com.nimbits.server.xmpp.XmppServiceFactory;

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
    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }



    @Override
    public List<Subscription> getSubscriptionsToPoint(Point point) {
        return SubscriptionTransactionFactory.getInstance(null).getSubscriptionsToPoint(point);
    }

    @Override
    public void updateSubscriptionLastSent(Subscription subscription) {
        SubscriptionTransactionFactory.getInstance(null).updateSubscriptionLastSent(subscription);
    }



    @Override
    public void processSubscriptions(final Point point, final Value v) throws NimbitsException {


        List<Subscription> subscriptions= getSubscriptionsToPoint(point);
        log.info("processing " + subscriptions.size() + "subscriptions");

        for (Subscription subscription : subscriptions) {

            if (subscription.getLastSent().getTime() + (subscription.getMaxRepeat() * 60 * 1000) < new Date().getTime()) {


                log.info("Processing Subscription " + subscription.getKey());
                Entity subscriptionEntity = EntityServiceFactory.getInstance().getEntityByKey(subscription.getKey());
                if (subscriptionEntity != null ) { //todo - handle subscribed to object deleted

                    Entity entity = EntityServiceFactory.getInstance().getEntityByKey(point.getKey());

                    User subscriber = UserServiceFactory.getInstance().getUserByUUID(subscriptionEntity.getOwner());
                    AlertType alert = v.getAlertState();

                    switch (subscription.getSubscriptionType()) {


                        case none:
                            break;
                        case anyAlert:
                            if (! alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                                sendNotification(subscriber, entity, subscription, point, v);
                            }
                            break;
                        case high:
                            if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn() ) {
                                sendNotification(subscriber, entity, subscription, point, v);
                            }
                            break;
                        case low:
                            if (alert.equals(AlertType.LowAlert) && point.isLowAlarmOn()) {
                                sendNotification(subscriber, entity, subscription, point, v);
                            }
                            break;
                        case idle:
                            if (alert.equals(AlertType.IdleAlert) && point.isIdleAlarmOn()) {
                                sendNotification(subscriber, entity, subscription, point, v);
                            }
                            break;
                        case newValue:
                            sendNotification(subscriber, entity, subscription, point, v);
                        case changed:
                            break;
                    }

                }
                else {


                }

            }




        }

    }


    @Override
    public Entity subscribe(Entity entity, Subscription subscription, EntityName name) throws NimbitsException {
        User user = getUser();
        if (entity.getEntityType().equals(EntityType.subscription)) {
            entity.setName(name);
            SubscriptionTransactionFactory.getInstance(user).subscribe(entity,subscription);
            return  EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

        }
        else { //new

            if (entity.getOwner().equals(user.getKey())) {   //subscribe to your own data
                Entity s = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                        ProtectionLevel.onlyMe, entity.getKey(), user.getKey());
                subscription.setUuid(s.getKey());
                SubscriptionTransactionFactory.getInstance(user).subscribe(s, subscription);
                return  EntityServiceFactory.getInstance().addUpdateEntity(user, s);
            }
            else { //subscribe to some elses data
                Entity s = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                        ProtectionLevel.onlyMe,  user.getKey(), user.getKey());
                subscription.setUuid(s.getKey());
                SubscriptionTransactionFactory.getInstance(user).subscribe(s, subscription);
                return  EntityServiceFactory.getInstance().addUpdateEntity(user, s);
            }
        }

    }

    @Override
    public Subscription readSubscription(Entity entity) throws NimbitsException {
        return SubscriptionTransactionFactory.getInstance(getUser()).readSubscription(entity);
    }

    @Override
    public Entity getSubscribedEntity(Entity entity) throws NimbitsException {
        Subscription subscription =
                SubscriptionTransactionFactory.getInstance(getUser()).readSubscription(entity);
        return EntityServiceFactory.getInstance().getEntityByKey(getUser(), subscription.getSubscribedEntity());

    }

    @Override
    public void deleteSubscription(User u, Entity entity) {
        SubscriptionTransactionFactory.getInstance(u).deleteSubscription(entity);
    }

    private void sendNotification(User user, Entity entity, Subscription subscription, Point point, Value value) throws NimbitsException {
        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailServiceFactory.getInstance().sendAlert(entity, point, user.getEmail(), value);
                break;
            case facebook:
                postToFB(point,entity, user, value);
                break;
            case twitter:
                sendTweet(user, entity, point, value);
                break;
            case instantMessage:
                doXMPP(user, subscription, entity, point, value);
                break;
            case feed:
                FeedServiceFactory.getInstance().postToFeed(user, entity, point, value, FeedType.data);
                break;
        }
    }
    private void doXMPP(final User u, Subscription subscription, Entity entity, final Point point, final Value v) throws NimbitsException {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getDoubleValue();
        }

        List<XmppResource> resources =  XmppServiceFactory.getInstance().getPointXmppResources(u, point);
        if (resources.size() > 0) {
            log.info("Sending XMPP with resources count: " + resources.size());
            XmppServiceFactory.getInstance().sendMessage(resources, message, u.getEmail());
        }
        else {
            XmppServiceFactory.getInstance().sendMessage(message, u.getEmail());
        }

    }


    private void sendTweet(User u, Entity entity, Point point, Value v) throws NimbitsException {
        StringBuilder message = new StringBuilder();
        message.append("#").append(entity.getName().getValue()).append(" ");
        message.append("Value=").append(v.getDoubleValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(" ").append(v.getNote());
        }
        message.append(" via #Nimbits");
        TwitterServiceFactory.getInstance().sendTweet(u, message.toString());
    }

    private void postToFB(final Point p, Entity entity, final User u, final Value v) throws NimbitsException {

        String m = ("Data Point #" + entity.getName().getValue() + " = " + v);
        if (v.getNote() != null) {
            m += " " + v.getNote();
        }

        StringBuilder picture = new StringBuilder();



        if (entity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

            List<Value> values = RecordedValueServiceFactory.getInstance().getTopDataSeries(p, 10).getValues();
            if (values.size() > 0) {

                picture.append("http://chart.apis.google.com/chart?chd=t:");
                for (Value vx : values) {
                    picture.append(vx.getDoubleValue()).append(",");
                }
                picture.deleteCharAt(picture.length()-1);
                picture.append("&chs=100x100&cht=ls&chco=3072F3&chds=0,105&chdlp=b&chls=2,4,1&chma=5,5,5,25&chds=a");
            }
            else {
                picture.append("http://app.nimbits.com/resources/images/logo.png");
            }



        } else {
            picture.append("http://app.nimbits.com/resources/images/logo.png");
        }

        String link = "http://app.nimbits.com?uuid=" + p.getKey();
        String d = Utils.isEmptyString(entity.getDescription()) ? "" : entity.getDescription();
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
                "nimbits.com", d);


    }
}
