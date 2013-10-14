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

package com.nimbits.cloudplatform.server.transactions.value;

import com.nimbits.cloudplatform.PMF;
import com.nimbits.cloudplatform.server.transactions.cache.CacheFactory;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.value.dao.ValueCache;
import com.nimbits.cloudplatform.server.transactions.value.dao.ValueCacheImpl;
import com.nimbits.cloudplatform.server.transactions.value.dao.ValueDao;
import com.nimbits.cloudplatform.server.transactions.value.dao.ValueDaoImpl;
import com.nimbits.cloudplatform.server.transactions.value.service.ValueService;
import com.nimbits.cloudplatform.server.transactions.value.service.ValueServiceImpl;

import javax.jdo.PersistenceManagerFactory;

/**
 * Created by benjamin on 10/14/13.
 */
public class ValueServiceFactory {

    public static ValueService getInstance() {
        NimbitsCache  cache = CacheFactory.getInstance();
        PersistenceManagerFactory pmf = PMF.get();

        return new ValueServiceImpl(pmf, cache);
    }


    public static ValueCache getCacheInstance(PersistenceManagerFactory pmf, NimbitsCache cache) {
        return new ValueCacheImpl(pmf, cache);

    }
    public static ValueDao getDaoInstance(PersistenceManagerFactory pmf) {
        return new ValueDaoImpl(pmf);

    }
}
