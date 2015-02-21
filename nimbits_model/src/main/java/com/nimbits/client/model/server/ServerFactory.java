package com.nimbits.client.model.server;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.apikey.AccessCode;

public class ServerFactory {

    public static Server getInstance(final UrlContainer url, final EmailAddress emailAddress, final AccessCode accessCode) {
        return new ServerModel(url, emailAddress, accessCode);
    }


}
