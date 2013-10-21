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

package com.nimbits.server.transaction.entity;

import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.entity.cache.EntityCache;
import com.nimbits.server.transaction.entity.cache.EntityCacheImpl;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.dao.EntityDaoImpl;
import com.nimbits.server.transaction.entity.service.EntityService;

/**
 * Created by benjamin on 10/14/13.
 */
public class EntityServiceFactory {

    public static EntityService getInstance(NimbitsEngine engine) {
         return new EntityServiceImpl(engine);
    }


    public static EntityCache getCacheInstance(NimbitsEngine engine) {
        return new EntityCacheImpl(engine);

    }
    public static EntityDao getDaoInstance(NimbitsEngine engine) {
        return new EntityDaoImpl(engine);

    }
}
