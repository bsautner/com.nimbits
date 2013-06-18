package com.nimbits.client.model.xmpp;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:42 PM
 */
public class XmppResourceModel extends EntityModel implements XmppResource {

    private String entity;

    public XmppResourceModel(XmppResource resource) throws NimbitsException {
        super(resource);
        this.entity = resource.getEntity();
    }

    public XmppResourceModel(Entity entity, String key) throws NimbitsException {
        super(entity);
        this.entity = key;
    }


    @Override
    public String getEntity() {
        return entity;
    }
}
