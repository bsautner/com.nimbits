package com.nimbits.server.dao.subscription;

import com.nimbits.*;
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
public class SubscriptionDaoImpl implements SubscriptionTransactions {
    final private User user;

    public SubscriptionDaoImpl(User u) {
        this.user = u;
    }


    @Override
    public Subscription subscribe(Subscription subscription) {
        return addOrUpdateSubscription(subscription);
    }

    private Subscription addOrUpdateSubscription(Subscription subscription)  {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<SubscriptionEntity> results;
        Subscription retObj = null;

        try {

            Query q = pm.newQuery(SubscriptionEntity.class, "subscriberUUID==u && subscribedPointUUID==p");
            q.declareParameters("String u, String p");
            q.setRange(0, 1);
            results = (List<SubscriptionEntity>) q.execute(user.getUuid(), subscription.getSubscribedPointUUID());
            if (results.size() > 0) {
                SubscriptionEntity result = results.get(0);
                Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setCategoryId(subscription.getCategoryId());
                result.setAlertStateChangeMethod(subscription.getAlertStateChangeMethod());
                result.setPropertyChangeMethod(subscription.getAlertStateChangeMethod());
                result.setDataUpdateAlertMethod(subscription.getAlertStateChangeMethod());
                result.setLastSent(subscription.getLastSent());
                result.setMaxRepeat(subscription.getMaxRepeat());
                result.setSubscribedPointUUID(subscription.getSubscribedPointUUID());
                result.setSubscriberUUID(subscription.getSubscriberUUID());
                tx.commit();
                pm.flush();
                retObj = SubscriptionFactory.createSubscription(result);

            }
            else {
                SubscriptionEntity entity = new SubscriptionEntity(subscription);
                pm.makePersistent(entity);
                retObj = SubscriptionFactory.createSubscription(entity);

            }
            return retObj;
        }
        finally {
            pm.close();
        }

    }
    public Subscription readSubscription(final Point point)  {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<SubscriptionEntity> results;
        Subscription retObj = null;
        try {
            Query q = pm.newQuery(SubscriptionEntity.class, "subscriberUUID==u && subscribedPointUUID==p");
            q.declareParameters("String u, String p");
            q.setRange(0, 1);
            results = (List<SubscriptionEntity>) q.execute(user.getUuid(), point.getUUID());
            if (results.size() > 0) {
                SubscriptionEntity result = results.get(0);
                retObj = SubscriptionFactory.createSubscription(result);
            }
            return retObj;
        }
        finally {
            pm.close();
        }

    }
}
