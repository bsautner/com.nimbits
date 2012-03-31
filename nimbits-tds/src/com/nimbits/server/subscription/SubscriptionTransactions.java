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

import com.nimbits.client.enums.SubscriptionType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 4:18 PM
 */
public interface SubscriptionTransactions {


    void subscribe(final Entity entity, final Subscription subscription);

    Subscription readSubscription(final Entity entity);

    List<Subscription> getSubscriptionsToPointByType(final Point point, final SubscriptionType type);

    List<Subscription> getSubscriptionsToPoint(final Point point);

    void updateSubscriptionLastSent(final Subscription subscription);

    void deleteSubscription(final Entity entity);
}
