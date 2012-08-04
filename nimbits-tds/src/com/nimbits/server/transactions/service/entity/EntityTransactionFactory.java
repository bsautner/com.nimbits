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

package com.nimbits.server.transactions.service.entity;

import com.nimbits.client.model.user.User;
import com.nimbits.server.transactions.dao.entity.EntityDaoImpl;
import com.nimbits.server.transactions.memcache.entity.EntityCacheImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 11:35 AM
 */
public class EntityTransactionFactory {


    private EntityTransactionFactory() {
    }




    private static class MapHolder {
        static final Map<String, EntityTransactions> daoMap = new HashMap<String, EntityTransactions>(100);
        static final Map<String, EntityTransactions> cacheMap = new HashMap<String, EntityTransactions>(100);
        private MapHolder() {
        }
    }
    public static EntityTransactions getInstance(final User user) {
        if (MapHolder.cacheMap.containsKey(user.getKey())) {
            return MapHolder.cacheMap.get(user.getKey());
        }
        else {
            EntityTransactions instance =  new EntityCacheImpl(user);
            MapHolder.cacheMap.put(user.getKey(), instance);
            return instance;
        }

    }

    public static EntityTransactions getDaoInstance(final User user) {
        if (MapHolder.daoMap.containsKey(user.getKey())) {
            return MapHolder.daoMap.get(user.getKey());
        }
        else {
            EntityTransactions instance =  new EntityDaoImpl(user);
            MapHolder.daoMap.put(user.getKey(), instance);
            return instance;
        }
    }

}
