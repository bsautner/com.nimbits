/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.xmpp;

import com.google.appengine.api.xmpp.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.xmpp.*;
import com.nimbits.client.service.xmpp.XMPPService;
import com.nimbits.server.entity.*;
import com.nimbits.server.user.*;

import java.util.*;
import java.util.logging.*;

public class XmppServiceImpl extends RemoteServiceServlet implements XMPPService {

    private static final Logger log = Logger.getLogger(XmppServiceImpl.class.getName());

    private static final long serialVersionUID = 1L;
    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }

    }
    public void sendMessage(final String msgBody, final EmailAddress email) {


        final JID jid = new JID(email.getValue());


        send(msgBody, jid);


    }

    private static void send(final String msgBody, final JID jid) {
        final Message msg = new MessageBuilder()
                .withRecipientJids(jid)
                .withBody(msgBody)
                .build();
        final com.google.appengine.api.xmpp.XMPPService xmpp = XMPPServiceFactory.getXMPPService();

        xmpp.sendMessage(msg);
    }

    @Override
    public void sendMessage(final List<XmppResource> resources, final String message, final EmailAddress email) throws NimbitsException {
        for (XmppResource resource : resources) {
            Entity entity = EntityServiceFactory.getInstance().getEntityByKey(resource.getKey());
            final JID jid;
            if (entity != null) {
              jid = new JID(email.getValue() + "/" + entity.getName().getValue());
                send(message, jid);
                log.info("stanza sent with jid: " + email.getValue() + "/" + entity.getName().getValue());
            }
        }
    }

    @Override
    public void deleteResource(User u, Entity entity) {
      XmppTransactionFactory.getInstance(u).deleteResource(entity);
    }

    @Override
    public List<XmppResource> getPointXmppResources(User user, Point point) {
        return XmppTransactionFactory.getInstance(user).getPointXmppResources(point);
    }

    @Override
    public Entity createXmppResource(final Entity targetPointEntity, final EntityName resourceName) throws NimbitsException {
        User u = getUser();
        if (u != null) {

            Entity entity = EntityModelFactory.createEntity(resourceName, "", EntityType.resource, ProtectionLevel.onlyMe,
                    targetPointEntity.getKey(),getUser().getKey() );
            Entity retObj = EntityServiceFactory.getInstance().addUpdateEntity(entity);
            XmppResource resource = XmppResourceFactory.createXmppResource(entity.getKey(), targetPointEntity.getKey());
            XmppTransactionFactory.getInstance(u).addResource(entity, resource);
            return retObj;
        }
        else {
            return null;
        }
    }



    @Override
    public void sendInvite() throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        final JID jid = new JID(u.getEmail().getValue());
        final com.google.appengine.api.xmpp.XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        xmpp.sendInvitation(jid);


    }

}
