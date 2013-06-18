package com.nimbits.client.model.xmpp;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:41 PM
 */
public class XmppResourceFactory {

    public static XmppResource createXmppResource(XmppResource resource) throws NimbitsException {
        return new XmppResourceModel(resource);

    }

    public static XmppResource createXmppResource(Entity entity, String key) throws NimbitsException {
        return new XmppResourceModel(entity, key);
    }
}
