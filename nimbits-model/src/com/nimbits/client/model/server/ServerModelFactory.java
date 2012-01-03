package com.nimbits.client.model.server;

import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;

import java.math.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:57 PM
 */
public class ServerModelFactory {

       public static Server createServer(final Server server) {

        return new ServerModel(server);

    }
      public static Server createServer(final String baseUrl, final EmailAddress ownerEmail, final String serverVersion) {
         return new ServerModel(baseUrl, ownerEmail, serverVersion);

    }

}
