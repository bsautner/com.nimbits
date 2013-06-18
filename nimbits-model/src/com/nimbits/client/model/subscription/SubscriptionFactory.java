package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:06 PM
 */
public class SubscriptionFactory {

    private SubscriptionFactory() {
    }

    public static Subscription createSubscription(Subscription subscription) throws NimbitsException {
        return new SubscriptionModel(subscription);

    }

    public static Subscription createSubscription(
            final Entity entity,
            final String subscribedEntity,
            final SubscriptionType type,
            final SubscriptionNotifyMethod method,
            final int maxRepeatSeconds,
            final boolean formatJson,
            final boolean enabled) throws NimbitsException {
        return new SubscriptionModel(entity,
                subscribedEntity,
                type,
                method,
                maxRepeatSeconds,
                formatJson, enabled
        );

    }
    public static List<Subscription> createSubscriptions(Collection<Subscription> subscriptions) throws NimbitsException {
        final List<Subscription> retObj = new ArrayList<Subscription>(subscriptions.size());
        for (final Subscription s : subscriptions) {
            retObj.add(createSubscription(s));
        }
        return retObj;
    }

}
