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
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.twitter.*;
import com.nimbits.server.user.*;
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
        for (Subscription subscription : subscriptions) {

            if (subscription.getLastSent().getTime() + (subscription.getMaxRepeat() * 60 * 1000) < new Date().getTime()) {



                Entity subscriptionEntity = EntityServiceFactory.getInstance().getEntityByUUID(subscription.getUuid());
                Entity entity = EntityServiceFactory.getInstance().getEntityByUUID(point.getUUID());

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




        }

    }


    @Override
    public Entity subscribe(Entity entity, Subscription subscription, EntityName name) throws NimbitsException {
        User user = getUser();
        if (entity.getEntityType().equals(EntityType.subscription)) {
            entity.setName(name);
            SubscriptionTransactionFactory.getInstance(user).subscribe(entity,subscription);
            return  EntityServiceFactory.getDaoInstance(user).addUpdateEntity(entity);

        }
        else { //new
            subscription.setUuid(UUID.randomUUID().toString());
            if (entity.getOwner().equals(user.getUuid())) {   //subscribe to your own data
                Entity s = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                        ProtectionLevel.onlyMe, subscription.getUuid(), entity.getEntity(), user.getUuid());
                SubscriptionTransactionFactory.getInstance(user).subscribe(s, subscription);
                return EntityServiceFactory.getDaoInstance(user).addUpdateEntity(s);
            }
            else { //subscribe to some elses data
                Entity s = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                        ProtectionLevel.onlyMe, subscription.getUuid(), user.getUuid(), user.getUuid());
                SubscriptionTransactionFactory.getInstance(user).subscribe(s, subscription);
                return EntityServiceFactory.getDaoInstance(user).addUpdateEntity(s);
            }
        }

    }

    @Override
    public Subscription readSubscription(Entity entity) throws NimbitsException {
        return SubscriptionTransactionFactory.getInstance(getUser()).readSubscription(entity);
    }

    @Override
    public Entity getSubscribedEntity(Entity entity) {
        Subscription subscription =
                SubscriptionTransactionFactory.getInstance(getUser()).readSubscription(entity);
        return EntityServiceFactory.getDaoInstance(getUser()).getEntityByUUID(subscription.getSubscribedEntity());

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
                FeedServiceFactory.getInstance().postToFeed(user, entity, point, value);
                break;
        }
    }
    private void doXMPP(final User u, Subscription subscription, Entity entity, final Point point, final Value v) {
        final String message;

        if (subscription.getNotifyFormatJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + entity.getName().getValue()
                    + "] updated to new value: " + v.getNumberValue();
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


    private void sendTweet(User u, Entity entity, Point point, Value v)  {
        StringBuilder message = new StringBuilder();
        message.append("#").append(entity.getName().getValue()).append(" ");
        message.append("Value=").append(v.getNumberValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(" ").append(v.getNote());
        }
        message.append(" via #Nimbits");
        TwitterServiceFactory.getInstance().sendTweet(u, message.toString());
    }

    private void postToFB(final Point p, Entity entity, final User u, final Value v) {

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
                    picture.append(vx.getNumberValue()).append(",");
                }
                picture.deleteCharAt(picture.length()-1);
                picture.append("&chs=100x100&cht=ls&chco=3072F3&chds=0,105&chdlp=b&chls=2,4,1&chma=5,5,5,25&chds=a");
            }
            else {
                picture.append("http://app.nimbits.com/resources/images/logo.png");
            }

//                picture = "http://app.nimbits.com" +
//                        "/service/chartapi?" +
//                        "point=" +  URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING) +
//                        "&email=" + URLEncoder.encode(u.getEmail().getValue(), Const.CONST_ENCODING) +
//                        "&cht=lc" +
//                        "&chs=100x100" +
//                        "&chds=a";


        } else {
            picture.append("http://app.nimbits.com/resources/images/logo.png");
        }

        // String link = "http://app.nimbits.com?view=chart&uuid=" + p.getUuid();
        String link = "http://app.nimbits.com?uuid=" + p.getUUID();
        String d = Utils.isEmptyString(entity.getDescription()) ? "" : entity.getDescription();
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
                "nimbits.com", d);


    }
}
