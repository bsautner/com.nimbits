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

package com.nimbits.cloudplatform.server.transactions.entity;

import com.nimbits.cloudplatform.PMF;
import com.nimbits.cloudplatform.server.transactions.cache.CacheFactory;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.entity.cache.EntityCache;
import com.nimbits.cloudplatform.server.transactions.entity.cache.EntityCacheImpl;
import com.nimbits.cloudplatform.server.transactions.entity.dao.EntityDao;
import com.nimbits.cloudplatform.server.transactions.entity.dao.EntityDaoImpl;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;

import javax.jdo.PersistenceManagerFactory;

/**
 * Created by benjamin on 10/14/13.
 */
public class EntityServiceFactory {

    public static EntityService getInstance() {
        NimbitsCache  cache = CacheFactory.getInstance();
        PersistenceManagerFactory pmf = PMF.get();

        return new EntityServiceImpl(pmf, cache);
    }


    public static EntityCache getCacheInstance(PersistenceManagerFactory pmf, NimbitsCache cache) {
        return new EntityCacheImpl(pmf, cache);

    }
    public static EntityDao getDaoInstance(PersistenceManagerFactory pmf) {
        return new EntityDaoImpl(pmf);

    }
}
