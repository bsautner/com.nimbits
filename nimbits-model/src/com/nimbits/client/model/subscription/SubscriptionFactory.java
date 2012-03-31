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

    public static Subscription createSubscription(
            final String subscribedEntity,
            final SubscriptionType type,
            final SubscriptionNotifyMethod method,
            final double maxRepeat,
            final Date lastSent,
            final boolean formatJson,
            final boolean enabled) {
        return new SubscriptionModel(
                subscribedEntity,
                type,
                method,
                maxRepeat, lastSent,
                formatJson, enabled
        );

    }
    public static List<Subscription> createSubscriptions(List<Subscription> subscriptions) {
        final ArrayList<Subscription> retObj = new ArrayList<Subscription>();
        for (final Subscription s : subscriptions) {
            retObj.add(createSubscription(s));
        }
        return retObj;
    }

}
