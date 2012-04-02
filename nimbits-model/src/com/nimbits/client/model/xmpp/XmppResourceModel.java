package com.nimbits.client.model.xmpp;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:42 PM
 */
public class XmppResourceModel implements XmppResource {
    private String uuid;
    private String entity;

    public XmppResourceModel(XmppResource resource) {
        this.uuid = resource.getKey();
        this.entity = resource.getEntity();
    }
    public XmppResourceModel(String uuid, String entity) {
        this.uuid = uuid;
        this.entity = entity;
    }

    @Override
    public String getKey() {
        return uuid;
    }

    @Override
    public String getEntity() {
        return entity;
    }
}
