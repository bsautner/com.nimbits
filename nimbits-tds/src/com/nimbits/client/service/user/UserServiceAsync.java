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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import java.util.List;

public interface UserServiceAsync {

    void updateSecret(final AsyncCallback<String> asyncCallback) throws NimbitsException;

    void getSecret(final AsyncCallback<String> asyncCallback) throws NimbitsException;

    void sendConnectionRequest(final EmailAddress email, final AsyncCallback<Void> asyncCallback) throws NimbitsException;

    void getPendingConnectionRequests(final EmailAddress email, final AsyncCallback<List<Connection>> asyncCallback);

    void connectionRequestReply(final EmailAddress targetEmail, final EmailAddress requestorEmail, final String uuid, boolean accepted, AsyncCallback<Void> asyncCallback) throws NimbitsException;

    void getConnections(final EmailAddress email, final AsyncCallback<List<User>> asyncCallback);

//    void getNimbitsUser(HttpServletRequest req,
//                        AsyncCallback<User> callback);
//
//    void checkLoggedIn(AsyncCallback<Void> callback);
//
//    void getAppUserUsingGoogleAuth(AsyncCallback<User> callback);
//
//    public void sendConnectionRequest(String email, AsyncCallback<Void> asyncCallback);
//
//    public void getPendingConnectionRequests(String email, AsyncCallback<List<Connection>> asyncCallback);
//
//    public void connectionRequestReply(String targetEmail, String RequestorEmail, String uuid, boolean accepted, AsyncCallback<Void> asyncCallback);
//
//    void getConnections(String email, AsyncCallback<Set<User>> callback);
//
//    void getHttpRequestUser(HttpServletRequest req, String caller, AsyncCallback<User> async);
//
//    void getConnections(String email, AsyncCallback<List<User>> async);

    //     public void checkLoggedIn() throws NotLoggedInException;
    //
    void getAppUserUsingGoogleAuth(AsyncCallback<User> async) throws NimbitsException;
}
