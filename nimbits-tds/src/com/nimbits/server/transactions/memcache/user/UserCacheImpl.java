/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.memcache.user;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.connection.ConnectionRequest;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transactions.dao.user.UserDAOImpl;
import com.nimbits.server.transactions.service.user.UserTransactions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/17/12
 * Time: 6:41 PM
 */
@Component("userCache")
public class UserCacheImpl implements UserTransactions {
    private UserDAOImpl userDao;
    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();


    @Override
    public List<User> getAllUsers(String sortColumn, int count) {
        return userDao.getAllUsers(sortColumn, count);
    }

    @Override
    public ConnectionRequest makeConnectionRequest(User u, EmailAddress emailAddress) throws NimbitsException {
        return userDao.makeConnectionRequest(u, emailAddress);
    }

    @Override
    public List<ConnectionRequest> getPendingConnectionRequests(EmailAddress emailAddress) throws NimbitsException {
        return userDao.getPendingConnectionRequests(emailAddress);
    }

    @Override
    public void updateConnectionRequest(Long key, User requestor, User acceptor, boolean accepted) throws NimbitsException {
       userDao.updateConnectionRequest(key, requestor, acceptor, accepted);
    }

    @Override
    public List<User> getConnectionRequests(List<String> connections) throws NimbitsException {
        return userDao.getConnectionRequests(connections);
    }

    @Override
    public List<User> getCachedAuthenticatedUser(String cacheKey) {
        if (cache.contains(cacheKey))  {
            User user = (User) cache.get(cacheKey);
            return Arrays.asList(user);
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public void cacheAuthenticatedUser( final String cacheKey, final User user) {
       if (cache.contains(cacheKey)) {
           cache.delete(cacheKey);
       }
        cache.put(cacheKey, user, Expiration.byDeltaSeconds(500));


    }

    public void setUserDao(UserDAOImpl userDao) {
        this.userDao = userDao;
    }

    public UserDAOImpl getUserDao() {
        return userDao;
    }
}
