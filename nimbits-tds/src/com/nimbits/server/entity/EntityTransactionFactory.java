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

package com.nimbits.server.entity;

import com.nimbits.client.model.user.User;
import com.nimbits.server.dao.entity.EntityDaoImpl;
import com.nimbits.server.memcache.entity.EntityCacheImpl;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 11:35 AM
 */
public class EntityTransactionFactory {

    public static EntityTransactions getInstance(User user) {
        return new EntityCacheImpl(user);
    }

    public static EntityTransactions getDaoInstance(User user) {
        return new EntityDaoImpl(user);
    }

}
