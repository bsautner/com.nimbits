package com.nimbits.client.model.xmpp;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:41 PM
 */
public class XmppResourceFactory {

    public static XmppResource createXmppResource(XmppResource resource) {
        return new XmppResourceModel(resource);

    }
    public static XmppResource createXmppResource(String uuid, String entity) {
        return new XmppResourceModel(uuid, entity);

    }
}
