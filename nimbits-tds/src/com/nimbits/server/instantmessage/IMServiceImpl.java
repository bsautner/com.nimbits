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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.instantmessage;

import com.google.appengine.api.xmpp.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.instantmessage.IMService;
import com.nimbits.server.user.UserServiceFactory;

public class IMServiceImpl extends RemoteServiceServlet implements IMService {

    //  private static final Logger log = Logger.getLogger(RecordValueTask.class.getValue());

    private static final long serialVersionUID = 1L;

    public void sendMessage(final String msgBody, final EmailAddress email) {
        final JID jid = new JID(email.getValue());

        final Message msg = new MessageBuilder()
                .withRecipientJids(jid)

                .withBody(msgBody)
                .build();
        final XMPPService xmpp = XMPPServiceFactory.getXMPPService();

        xmpp.sendMessage(msg);


    }

    @Override
    public void sendInvite() throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        final JID jid = new JID(u.getEmail().getValue());
        final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        xmpp.sendInvitation(jid);


    }

}
