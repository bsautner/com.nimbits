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

package com.nimbits.server.transaction.user.cache;

import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.cache.NimbitsCache;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class UserCacheImpl implements UserCache {

    private final NimbitsCache cache;

    public UserCacheImpl(NimbitsEngine engine) {
        this.cache = engine.getCache();
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
    public void cacheAuthenticatedUser(final String cacheKey, final User user) {
       if (cache.contains(cacheKey)) {
           cache.delete(cacheKey);
       }
        cache.put(cacheKey, user);


    }


}
