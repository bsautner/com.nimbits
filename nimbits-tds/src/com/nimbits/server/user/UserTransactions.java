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

package com.nimbits.server.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import twitter4j.auth.AccessToken;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface UserTransactions {

    User createNimbitsUser(final EmailAddress emailAddress) throws NimbitsException;

    User setFacebookToken(final EmailAddress emailAddress, final String token, final long facebookId) throws NimbitsException;

    User getNimbitsUser(final EmailAddress emailAddress) throws NimbitsException;

    List<User> getAllUsers(final String sortColumn, int count);

    User updateSecret(final EmailAddress emailAddress, final UUID uuid) throws NimbitsException;

    Connection makeConnectionRequest(final User u, final EmailAddress emailAddress);

    List<Connection> getPendingConnectionRequests(final EmailAddress emailAddress);

    List<User> updateConnectionRequest(final String uuid, final User requestor, final User acceptor, final boolean accepted) throws NimbitsException;

    User updateTwitter(final EmailAddress emailAddress, final AccessToken token) throws NimbitsException;

    User updateLastLoggedIn(final User user, final Date LastLoggedIn) throws NimbitsException;

    User getUserByKey(final String subscriberUUID);

    List<User>  getConnectionRequests(final List<String> connections);


    List<User> getUsers();
}