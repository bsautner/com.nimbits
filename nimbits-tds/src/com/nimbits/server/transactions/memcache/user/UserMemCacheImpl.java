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

package com.nimbits.server.transactions.memcache.user;

import com.google.appengine.api.memcache.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.transactions.memcache.*;
import com.nimbits.server.transactions.service.user.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/27/11
 * Time: 11:18 AM
 */
@SuppressWarnings("unchecked")
public class UserMemCacheImpl implements UserTransactions {
    private final MemcacheService cache;

    public UserMemCacheImpl() {
        cache =  MemcacheServiceFactory.getMemcacheService();
    }



    protected  void addUserToCache(final User user) throws NimbitsException {
        if (cache.contains(MemCacheHelper.allUsersCacheKey)) {
            final Map<EmailAddress, User> users = (Map<EmailAddress, User>) cache.get(MemCacheHelper.allUsersCacheKey);
            if (users.containsKey(user.getEmail())) {
                users.remove(user.getEmail());
            }
            users.put(user.getEmail(), user);
        }

        if (cache.contains(MemCacheHelper.UserCacheKey(user))) {
            cache.delete(MemCacheHelper.UserCacheKey(user));
        }
        if (cache.contains(MemCacheHelper.UserCacheKey(user.getKey()))) {
            cache.delete(MemCacheHelper.UserCacheKey(user.getKey()));
        }
        cache.put(MemCacheHelper.UserCacheKey(user.getKey()), user);
        cache.put(MemCacheHelper.UserCacheKey(user.getEmail()), user);


    }

    protected User getUserFromCache(final EmailAddress emailAddress) {

        try {
            return cache.contains(MemCacheHelper.UserCacheKey(emailAddress)) ? (User) cache.get(MemCacheHelper.UserCacheKey(emailAddress)) : null;

        } catch (InvalidValueException e) {
            cache.delete(MemCacheHelper.UserCacheKey(emailAddress));
            return null;
        }

    }




//    @Override
//    public User setFacebookToken(final EmailAddress emailAddress, final String token, final long facebookId) throws NimbitsException {
//        final User u = UserTransactionFactory.getDAOInstance().setFacebookToken(emailAddress, token, facebookId);
//        addUserToCache(u);
//        return u;
//    }

//    @Override
//    public User getNimbitsUser(final EmailAddress emailAddress) throws NimbitsException {
//
//        User u = getUserFromCache(emailAddress);
//        if (u != null) {
//            return u;
//        } else {
//            u = UserTransactionFactory.getDAOInstance().getNimbitsUser(emailAddress);
//            if (u != null) {
//                addUserToCache(u);
//                return u;
//            } else {
//                return null;
//            }
//        }
//
//
//    }

    @Override
    public List<User> getAllUsers(final String sortColumn, final int count) {
//        if (cache.contains(allUsersCacheKey)) {
//            HashMap<EmailAddress, User> users = (HashMap<EmailAddress, User>) cache.get(allUsersCacheKey);
//            List<User> list = new ArrayList<User>();
//            for (User u : users.values()) {
//                list.add(u);
//            }
//            return list;
//        }
//        else {
        return UserTransactionFactory.getDAOInstance().getAllUsers(sortColumn, count);
//            Map<EmailAddress, User> userMap = new HashMap<EmailAddress, User>();
//            for (User u : store) {
//                userMap.put(u.getEmail(), u);
//            }
//            cache.put(allUsersCacheKey, userMap);
//            return store;
//        }

    }


//    @Override
//    public User updateSecret(final EmailAddress emailAddress, final UUID uuid) throws NimbitsException {
//
//        final User u = UserTransactionFactory.getDAOInstance().updateSecret(emailAddress, uuid);
//        addUserToCache(u);
//        return u;
//
//    }


    @Override
    public ConnectionRequest makeConnectionRequest(final User u, final EmailAddress email) throws NimbitsException {
        return UserTransactionFactory.getDAOInstance().makeConnectionRequest(u, email);
    }

    @Override
    public List<ConnectionRequest> getPendingConnectionRequests(final EmailAddress email) throws NimbitsException {
        return UserTransactionFactory.getDAOInstance().getPendingConnectionRequests(email);
    }

    @Override
    public void updateConnectionRequest(final Long key, final User requestor, final User acceptor, final boolean accepted) throws NimbitsException {
        UserTransactionFactory.getDAOInstance().updateConnectionRequest(key, requestor, acceptor, accepted);

    }

//    @Override
//    public User updateTwitter(final EmailAddress email, final AccessToken token) throws NimbitsException {
//        final User u = UserTransactionFactory.getDAOInstance().updateTwitter(email, token);
//        addUserToCache(u);
//        return u;
//
//    }
//
//    @Override
//    public User updateLastLoggedIn(final User user, final Date LastLoggedIn) throws NimbitsException {
//        final User u = UserTransactionFactory.getDAOInstance().updateLastLoggedIn(user, LastLoggedIn);
//        addUserToCache(u);
//        return u;
//    }

//    @Override
//    public User getUserByKey(final String subscriberUUID) throws NimbitsException {
//        return UserTransactionFactory.getDAOInstance().getUserByKey(subscriberUUID);
//    }

    @Override
    public List<User> getConnectionRequests(final List<String> connections) throws NimbitsException {
        return UserTransactionFactory.getDAOInstance().getConnectionRequests(connections);
    }

//    @Override
//    public List<User> getUsers() throws NimbitsException {
//        return UserTransactionFactory.getDAOInstance().getUsers();
//    }

}
