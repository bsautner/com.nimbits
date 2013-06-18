package com.nimbits.client.model.instance;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;

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
      public static Instance createInstance(final Entity baseEntity, final String baseUrl, final EmailAddress ownerEmail, final String serverVersion) throws NimbitsException {
         return new InstanceModel(baseEntity, baseUrl, ownerEmail, serverVersion);

    }

}
