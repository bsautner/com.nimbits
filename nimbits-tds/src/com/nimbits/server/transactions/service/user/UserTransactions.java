/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.transactions.service.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.connection.ConnectionRequest;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import java.util.List;

public interface UserTransactions {

    List<User> getAllUsers(final String sortColumn, int count);

    ConnectionRequest makeConnectionRequest(final User u, final EmailAddress emailAddress) throws NimbitsException;

    List<ConnectionRequest> getPendingConnectionRequests(final EmailAddress emailAddress) throws NimbitsException;

    void updateConnectionRequest(final Long key, final User requestor, final User acceptor, final boolean accepted) throws NimbitsException;

    List<User>  getConnectionRequests(final List<String> connections) throws NimbitsException;


}