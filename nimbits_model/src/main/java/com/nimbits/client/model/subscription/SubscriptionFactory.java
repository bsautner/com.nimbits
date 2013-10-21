/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:06 PM
 */
public class SubscriptionFactory {

    private SubscriptionFactory() {
    }

    public static Subscription createSubscription(Subscription subscription)  {
        return new SubscriptionModel(subscription);

    }

    public static Subscription createSubscription(
            final Entity entity,
            final String subscribedEntity,
            final SubscriptionType type,
            final SubscriptionNotifyMethod method,
            final int maxRepeatSeconds,
            final boolean formatJson,
            final boolean enabled)  {
        return new SubscriptionModel(entity,
                subscribedEntity,
                type,
                method,
                maxRepeatSeconds,
                formatJson, enabled
        );

    }
    public static List<Subscription> createSubscriptions(Collection<Subscription> subscriptions)  {
        final List<Subscription> retObj = new ArrayList<Subscription>(subscriptions.size());
        for (final Subscription s : subscriptions) {
            retObj.add(createSubscription(s));
        }
        return retObj;
    }

}
