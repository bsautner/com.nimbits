package com.nimbits.server.quota;

import com.nimbits.client.model.user.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:28 PM
 */
public class QuotaFactory {

    public static Quota getInstance(User user) {
          return new QuotaImpl(user);
    }

}
