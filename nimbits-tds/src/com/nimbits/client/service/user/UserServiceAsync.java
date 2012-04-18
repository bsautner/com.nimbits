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

package com.nimbits.client.service.user;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.LoginInfo;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;

import java.util.*;

public interface UserServiceAsync {


    void sendConnectionRequest(final EmailAddress email, final AsyncCallback<Void> asyncCallback);

    void getPendingConnectionRequests(final EmailAddress email, final AsyncCallback<List<ConnectionRequest>> asyncCallback) throws NimbitsException;

    void connectionRequestReply(final EmailAddress targetEmail, final EmailAddress requestorEmail, final Long key, final boolean accepted, final AsyncCallback<Void> asyncCallback);

    void getAppUserUsingGoogleAuth(final AsyncCallback<User> async);

    void getUserByKey(final String key, AuthLevel authLevel, final AsyncCallback<User> async) throws NimbitsException;

    void getConnectionRequests(final List<String> connections, final AsyncCallback<List<User>> async) throws NimbitsException;

    void login(String requestUri, AsyncCallback<LoginInfo> async);
}
