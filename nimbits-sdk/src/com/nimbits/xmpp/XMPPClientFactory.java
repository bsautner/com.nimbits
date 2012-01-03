package com.nimbits.xmpp;

import com.nimbits.client.NimbitsClient;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 11/22/11
 * Time: 4:11 PM
 */
public class XMPPClientFactory {
    private static XMPPClient instance;

    public static XMPPClient getInstance(NimbitsClient client, String appId) {

        //if  (instance==null) {
        instance = new XMPPClientImpl(client, appId);
        //  }
        return instance;

    }

}
