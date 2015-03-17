package com.nimbits.client.model.server;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.apikey.AccessToken;

public class ServerFactory {

    public static Server getInstance(final UrlContainer url, final EmailAddress emailAddress, final AccessToken accessToken) {
        return new ServerModel(url, emailAddress, accessToken);
    }


}
