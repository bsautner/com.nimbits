/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.communication.xmpp;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.service.xmpp.XMPPService;
import com.nimbits.cloudplatform.server.transactions.user.UserHelper;
import org.springframework.stereotype.Service;

@Service("xmppService")
public class XmppServiceImpl extends RemoteServiceServlet implements XMPPService {

   // private static final Logger log = Logger.getLogger(XmppServiceImpl.class.getName());

    private static final long serialVersionUID = 1L;

    public static void sendMessage(final String msgBody, final EmailAddress email) {


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
    public void sendInviteRpc()  {

        User user =  UserHelper.getUser();


        final JID jid = new JID(user.getEmail().getValue());
        final com.google.appengine.api.xmpp.XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        xmpp.sendInvitation(jid);


    }


}
