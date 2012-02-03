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

package com.nimbits.client.service.user;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;

import java.util.*;

public interface UserServiceAsync {

    void updateSecret(final AsyncCallback<String> asyncCallback);

    void getSecret(final AsyncCallback<String> asyncCallback);

    void sendConnectionRequest(final EmailAddress email, final AsyncCallback<Void> asyncCallback);

    void getPendingConnectionRequests(final EmailAddress email, final AsyncCallback<List<Connection>> asyncCallback);

    void connectionRequestReply(final EmailAddress targetEmail, final EmailAddress requestorEmail, final String uuid, boolean accepted, AsyncCallback<Void> asyncCallback);

    void getConnections(final EmailAddress email, final AsyncCallback<List<User>> asyncCallback);

    void getAppUserUsingGoogleAuth(AsyncCallback<User> async);

    void getUserByUUID(String subscriberUUID, AsyncCallback<User> async);
}
