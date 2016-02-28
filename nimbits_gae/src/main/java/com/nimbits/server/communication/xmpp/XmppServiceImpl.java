/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.communication.xmpp;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.springframework.stereotype.Service;


public class XmppServiceImpl implements XmppService {


    @Override
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


}
