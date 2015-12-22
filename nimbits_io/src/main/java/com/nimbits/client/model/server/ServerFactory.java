package com.nimbits.client.model.server;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.email.EmailAddress;

@Deprecated
public class ServerFactory {

    public static Server getInstance(final UrlContainer url, final EmailAddress emailAddress, final AccessKey accessToken) {
        return new ServerModel(url, emailAddress, accessToken);
    }


}
