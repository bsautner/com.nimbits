package com.nimbits.server.orm;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Key;
import com.nimbits.client.model.xmpp.*;

import javax.jdo.annotations.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:36 PM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class XmppResourceEntity implements XmppResource {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String uuid;

    @Persistent
    private String entity;

    public XmppResourceEntity() {
    }
    public XmppResourceEntity(XmppResource resource) {
        this.uuid = resource.getUuid();
        this.entity = resource.getEntity();
    }
    public Key getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEntity() {
        return entity;
    }
}
