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

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.xmpp.*;
import com.nimbits.client.service.subscription.*;
import com.nimbits.server.email.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.facebook.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.twitter.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;
import com.nimbits.server.xmpp.*;

import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:51 PM
 */
public class SubscriptionServiceImpl extends RemoteServiceServlet implements
        SubscriptionService {
    private static final Logger log = Logger.getLogger(SubscriptionServiceImpl.class.getName());
    private static final int SECONDS = 60;
    private static final int INT = 120;
    private static final int INT1 = 512;

    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }



    @Override
    public List<Subscription> getSubscriptionsToPoint(final Point point) {
        return SubscriptionTransactionFactory.getInstance(null).getSubscriptionsToPoint(point);
    }

    @Override
    public void updateSubscriptionLastSent(final Subscription subscription) {
        SubscriptionTransactionFactory.getInstance(null).updateSubscriptionLastSent(subscription);
    }



    @Override
    public void processSubscriptions(final User user, final Point point, final Value v) throws NimbitsException {


        final List<Subscription> subscriptions= getSubscriptionsToPoint(point);
        log.info("processing " + subscriptions.size() + " subscriptions");

        for (final Subscription subscription : subscriptions) {

            if (subscription.getLastSent().getTime() + (subscription.getMaxRepeat() * SECONDS * 1000) < new Date().getTime()) {


                log.info("Processing Subscription " + subscription.getKey());
                subscription.setLastSent(new Date());
                SubscriptionServiceFactory.getInstance().updateSubscriptionLastSent(subscription);
                final Entity subscriptionEntity = EntityServiceFactory.getInstance().getEntityByKey(user, subscription.getKey(), EntityStore.class.getName());
                //todo - handle subscribed to object deleted
                if (subscriptionEntity != null ) {

                    final Entity pointEntity = EntityServiceFactory.getInstance().getEntityByKey(null, point.getKey());

                    final User subscriber = UserServiceFactory.getInstance().getUserByKey(subscriptionEntity.getOwner());
                    final AlertType alert = v.getAlertState();

                    switch (subscription.getSubscriptionType()) {


                        case none:
                            break;
                        case anyAlert:
                            if (! alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                                sendNotification(subscriber, pointEntity, subscription, point, v);
                            }
                            break;
                        case high:
                            if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn() ) {
                                sendNotification(subscriber, pointEntity, subscription, point, v);
                            }
                            break;
                        case low:
                            if (alert.equals(AlertType.LowAlert) && point.isLowAlarmOn()) {
                                sendNotification(subscriber, pointEntity, subscription, point, v);
                            }
                            break;
                        case idle:
                            if (alert.equals(AlertType.IdleAlert) && point.isIdleAlarmOn()) {
                                sendNotification(subscriber, pointEntity, subscription, point, v);
                            }
                            break;
                        case newValue:
                            sendNotification(subscriber, pointEntity, subscription, point, v);
                            break;
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
    public Entity subscribe(final Entity entity, final Subscription subscription, final EntityName name) throws NimbitsException {
        final User user = getUser();
        if (entity.getEntityType().equals(EntityType.subscription)) {
            entity.setName(name);
            SubscriptionTransactionFactory.getInstance(user).subscribe(entity,subscription);
            return  EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

        }
        else { //new

            if (entity.getOwner().equals(user.getKey())) {   //subscribe to your own data
                final Entity s = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                        ProtectionLevel.onlyMe, entity.getKey(), user.getKey());
                final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, s);
                SubscriptionTransactionFactory.getInstance(user).subscribe(r, subscription);
                return  r;
            }
            else { //subscribe to some elses data
                final Entity s = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                        ProtectionLevel.onlyMe,  user.getKey(), user.getKey());

                SubscriptionTransactionFactory.getInstance(user).subscribe(s, subscription);
                return  EntityServiceFactory.getInstance().addUpdateEntity(user, s);
            }
        }

    }

    @Override
    public Subscription readSubscription(final Entity entity) throws NimbitsException {
        return SubscriptionTransactionFactory.getInstance(getUser()).readSubscription(entity);
    }

    @Override
    public Entity getSubscribedEntity(final Entity entity) throws NimbitsException {
        final Subscription subscription =
                SubscriptionTransactionFactory.getInstance(getUser()).readSubscription(entity);
        return EntityServiceFactory.getInstance().getEntityByKey(getUser(), subscription.getSubscribedEntity(), PointEntity.class.getName());

    }

    @Override
    public void deleteSubscription(final User u, final Entity entity) {
        SubscriptionTransactionFactory.getInstance(u).deleteSubscription(entity);
    }

    private static void sendNotification(final User user, final Entity entity, final Subscription subscription, final Point point, final Value value) throws NimbitsException {
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
                sendTweet(user, entity, value);
                break;
            case instantMessage:
                doXMPP(user, subscription, entity, point, value);
                break;
            case feed:
                FeedServiceFactory.getInstance().postToFeed(user, entity, point, value, FeedType.data);
                break;
        }
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

    private static void postToFB(final Point p, final Entity entity, final User u, final Value v) throws NimbitsException {

        String m = ("Data Point #" + entity.getName().getValue() + " = " + v.getDoubleValue());
        if (v.getNote() != null) {
            m += ' ' + v.getNote();
        }

        final StringBuilder picture = new StringBuilder(INT1);



        if (entity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

            final List<Value> values = RecordedValueServiceFactory.getInstance().getTopDataSeries(p, 10).getValues();
            if (values.isEmpty()) {
                picture.append("http://app.nimbits.com/resources/images/logo.png");
            } else {

                picture.append("http://chart.apis.google.com/chart?chd=t:");
                for (final Value vx : values) {
                    picture.append(vx.getDoubleValue()).append(',');
                }
                picture.deleteCharAt(picture.length() - 1);
                picture.append("&chs=100x100&cht=ls&chco=3072F3&chds=0,105&chdlp=b&chls=2,4,1&chma=5,5,5,25&chds=a");
            }



        } else {
            picture.append("http://app.nimbits.com/resources/images/logo.png");
        }

        final String link = "http://app.nimbits.com?uuid=" + p.getKey();
        final String d = Utils.isEmptyString(entity.getDescription()) ? "" : entity.getDescription();
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
                "nimbits.com", d);


    }
}
