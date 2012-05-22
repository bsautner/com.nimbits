package com.nimbits.client.model.instance;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:57 PM
 */
public class InstanceModelFactory {

    private InstanceModelFactory() {
    }

    public static Instance createInstance(final Instance server) throws NimbitsException {

        return new InstanceModel(server);

    }
      public static Instance createInstance(final String baseUrl, final EmailAddress ownerEmail, final String serverVersion) {
         return new InstanceModel(baseUrl, ownerEmail, serverVersion);

    }

}
