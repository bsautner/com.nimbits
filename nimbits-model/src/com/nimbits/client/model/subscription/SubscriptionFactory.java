package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:06 PM
 */
public class SubscriptionFactory {

    public static Subscription createSubscription(Subscription subscription) {
        return new SubscriptionModel(subscription);

    }

    public static Subscription createSubscription(final SubscriptionDeliveryMethod dataUpdateAlertMethod,
                                                  final SubscriptionDeliveryMethod alarmStateChangeMethod,
                                                  final SubscriptionDeliveryMethod propertyChangeMethod,
                                                  final double maxRepeat,
                                                  final Date lastSent) {
        return new SubscriptionModel(
                dataUpdateAlertMethod,
                alarmStateChangeMethod,
                propertyChangeMethod,
                maxRepeat, lastSent
                );

    }
    public static List<Subscription> createSubscriptions(List<Subscription> subscriptions) {
        ArrayList<Subscription> retObj = new ArrayList<Subscription>();
        for (Subscription s : subscriptions) {
            retObj.add(createSubscription(s));
        }
        return retObj;
    }

}
