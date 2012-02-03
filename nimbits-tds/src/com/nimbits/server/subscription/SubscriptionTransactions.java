package com.nimbits.server.subscription;

import com.nimbits.client.model.category.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 4:18 PM
 */
public interface SubscriptionTransactions {


    Subscription subscribe(Subscription subscription);
    Subscription readSubscription(final Point point);

    void deleteSubscription(Point point);

    Point moveSubscription(Point point, CategoryName newCategoryName);

    List<Subscription> getSubscriptionsToPoint(Point point);

    void updateSubscriptionLastSent(Subscription subscription);

}
