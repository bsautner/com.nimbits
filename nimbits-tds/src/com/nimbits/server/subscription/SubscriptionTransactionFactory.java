package com.nimbits.server.subscription;

import com.nimbits.client.model.user.*;
import com.nimbits.server.dao.subscription.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 4:20 PM
 */
public class SubscriptionTransactionFactory {
    private static SubscriptionTransactions instance;


    public static SubscriptionTransactions getInstance(final User u) {
        return new SubscriptionDaoImpl(u);
    }

}
