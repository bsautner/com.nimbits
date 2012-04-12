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

package com.nimbits.server.transactions.dao.subscription;

import com.nimbits.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.subscription.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 4:18 PM
 */
@SuppressWarnings({"unchecked", "unused"})
public class SubscriptionDaoImpl implements SubscriptionTransactions {

    public SubscriptionDaoImpl(final User u) {

    }


    private static SubscriptionEntity getSubscription(final PersistenceManager pm, final Entity entity) {
      return getSubscription(pm, entity.getKey());


    }
    private static SubscriptionEntity getSubscription(final PersistenceManager pm, final String key) {
        try {
            return pm.getObjectById(SubscriptionEntity.class, key);

        }
        catch (JDOObjectNotFoundException ex) {
            return null;
        }


    }


    @Override
    public List<Subscription> getSubscriptionsToPoint(final Entity point) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(SubscriptionEntity.class);
            q.setFilter("subscribedEntity==p && enabled==e");
            q.declareParameters("String p, Boolean e");
            final Collection<Subscription> results = (Collection<Subscription>) q.execute(point.getKey(), true);
            return SubscriptionFactory.createSubscriptions(results);
        }
        finally {
            pm.close();
        }
    }

//    @Override
//    public List<Subscription> getSubscriptionsToPointByType(final Point point, final SubscriptionType type) throws NimbitsException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        try {
//            final Query q = pm.newQuery(SubscriptionEntity.class, "subscribedEntity==p && subscriptionType==t && enabled==e" );
//            q.declareParameters("String p, Integer t, Boolean e");
//            final Collection<Subscription> results = (Collection<Subscription>) q.execute(point.getKey(), type.getCode(), true);
//            return SubscriptionFactory.createSubscriptions(results);
//        }
//        finally {
//            pm.close();
//        }
//    }




}
