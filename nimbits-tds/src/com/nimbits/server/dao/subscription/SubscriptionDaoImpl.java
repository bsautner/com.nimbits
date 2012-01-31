package com.nimbits.server.dao.subscription;

import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.subscription.*;

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
        return null;  //auto generated
    }
}
