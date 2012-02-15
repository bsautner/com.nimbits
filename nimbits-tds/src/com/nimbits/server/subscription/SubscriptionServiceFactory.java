package com.nimbits.server.subscription;

import com.nimbits.client.service.subscription.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:54 PM
 */
public class SubscriptionServiceFactory {



    public static SubscriptionService getInstance() {

        return new SubscriptionServiceImpl();
    }




}
