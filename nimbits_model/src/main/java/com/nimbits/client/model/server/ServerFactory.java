package com.nimbits.client.model.server;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.server.apikey.ApiKey;

public class ServerFactory {

    public static Server getInstance(final UrlContainer url, final ApiKey apiKey) {
            return new ServerModel(url, apiKey);
    }

    public static Server getInstance(final UrlContainer url) {
        return new ServerModel(url);
    }

}
