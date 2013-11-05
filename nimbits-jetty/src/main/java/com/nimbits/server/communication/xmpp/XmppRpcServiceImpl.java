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

package com.nimbits.server.communication.xmpp;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.xmpp.XmppRpcService;
import com.nimbits.server.ApplicationListener;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.user.UserHelper;


public class XmppRpcServiceImpl extends RemoteServiceServlet implements XmppRpcService {

    @Override
    public void sendInviteRpc() {
        NimbitsEngine engine = ApplicationListener.createEngine();
        User user = UserHelper.getUser(engine);





    }
}
