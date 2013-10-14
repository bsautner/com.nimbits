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

import com.nimbits.cloudplatform.server.transactions.cache.CacheFactory;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.user.cache.UserCache;
import com.nimbits.cloudplatform.server.transactions.user.cache.UserCacheImpl;
import com.nimbits.cloudplatform.server.transactions.user.service.UserService;
import com.nimbits.cloudplatform.server.transactions.user.service.UserServiceImpl;

/**
 * Created by benjamin on 9/1/13.
 */
public class UserServiceFactory {

    public static UserService getInstance() {
        return new UserServiceImpl();
    }


    public static UserCache getCacheInstance() {
        NimbitsCache cache = CacheFactory.getInstance();


        return new UserCacheImpl(cache);
    }
}
