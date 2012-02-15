package com.nimbits.server.subscription;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.subscription.*;
import com.nimbits.server.email.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.facebook.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.instantmessage.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.twitter.*;
import com.nimbits.server.user.*;
import com.nimbits.shared.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:51 PM
 */
public class SubscriptionServiceImpl extends RemoteServiceServlet implements
        SubscriptionService {

    @Override
    public void deleteSubscription(Point point) throws NimbitsException {
        //final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
        //      this.getThreadLocalRequest());
        //SubscriptionTransactionFactory.getInstance(u).deleteSubscription(point);
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
    public void processSubscriptions(final Point point, final Value v) {


        List<Subscription> subscriptions= getSubscriptionsToPoint(point);
        for (Subscription subscription : subscriptions) {

            if (subscription.getLastSent().getTime() + (subscription.getMaxRepeat() * 60 * 1000) < new Date().getTime()) {

                Entity subscriptionEntity = EntityTransactionFactory.getInstance(null).getEntityByUUID(subscription.getUuid());
                User subscriber = UserServiceFactory.getInstance().getUserByUUID(subscriptionEntity.getOwner());
                AlertType alert = v.getAlertState();

                switch (subscription.getSubscriptionType()) {


                    case none:
                        break;
                    case anyAlert:
                        if (! alert.equals(AlertType.OK) && (point.isHighAlarmOn() || point.isLowAlarmOn())) {
                            sendNotification(subscriber, subscription, point, v);
                        }
                        break;
                    case high:
                        if (alert.equals(AlertType.HighAlert) && point.isHighAlarmOn() ) {
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
                    case changed:
                        break;
                }



            }




        }

    }

    private void sendNotification(User user, Subscription subscription, Point point, Value value) {
        switch (subscription.getNotifyMethod()) {
            case none:
                break;
            case email:
                EmailServiceFactory.getInstance().sendAlert(point, user.getEmail(), value);
                break;
            case facebook:
                postToFB(point, user, value);
                break;
            case twitter:
                sendTweet(user, point, value);
                break;
            case instantMessage:
                doXMPP(user, point, value);
                break;
            case stream:
                break;
        }
    }
    private void doXMPP(final User u, final Point point, final Value v) {
        final String message;

        if (point.getSendAlertsAsJson()) {
            point.setValue(v);
            message = GsonFactory.getInstance().toJson(point);
        } else {
            message = "Nimbits Data Point [" + point.getName().getValue()
                    + "] updated to new value: " + v.getNumberValue();
        }


        IMFactory.getInstance().sendMessage(message, u.getEmail());
    }


    private void sendTweet(User u, Point point, Value v)  {
        StringBuilder message = new StringBuilder();
        message.append("#").append(point.getName().getValue()).append(" ");
        message.append("Value=").append(v.getNumberValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(" ").append(v.getNote());
        }
        message.append(" via #Nimbits");
        TwitterServiceFactory.getInstance().sendTweet(u, message.toString());
    }

    private void postToFB(final Point p, final User u, final Value v) {

        String m = ("Data Point #" + p.getName().getValue() + " = " + v);
        if (v.getNote() != null) {
            m += " " + v.getNote();
        }

        StringBuilder picture = new StringBuilder();



        if (p.isPublic()) {

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
        String d = Utils.isEmptyString(p.getDescription()) ? "" : p.getDescription();
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture.toString(), link, "Subscribe to this data feed.",
                "nimbits.com", d);


    }
}
