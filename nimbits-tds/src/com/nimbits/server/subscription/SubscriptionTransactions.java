package com.nimbits.server.subscription;

import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 4:18 PM
 */
public interface SubscriptionTransactions {


    Subscription subscribe(Subscription subscription);
    Subscription readSubscription(final Point point);

}
