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

package com.nimbits.cloudplatform.server.transactions.user;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.cloudplatform.client.model.user.User;
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
public class UserCache {

    private static MemcacheService cacheFactory;

    static {
        cacheFactory = MemcacheServiceFactory.getMemcacheService();
    }



    protected static List<User> getCachedAuthenticatedUser(String cacheKey) {
        if (cacheFactory.contains(cacheKey))  {
            User user = (User) cacheFactory.get(cacheKey);
            return Arrays.asList(user);
        }
        else {
            return Collections.emptyList();
        }
    }


    protected static void cacheAuthenticatedUser( final String cacheKey, final User user) {
       if (cacheFactory.contains(cacheKey)) {
           cacheFactory.delete(cacheKey);
       }
        cacheFactory.put(cacheKey, user, Expiration.byDeltaSeconds(3200));


    }


}
