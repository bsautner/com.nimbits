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

import com.nimbits.cloudplatform.PMF;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModelFactory;
import com.nimbits.cloudplatform.server.orm.UserEntity;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
@Repository("userDao")
public class UserDao {
    private static final Logger log = Logger.getLogger(UserDao.class.getName());
    private static final int MAX_REQUESTS = 25;
    public static final String STRING = "approved == a && targetEmail==e && rejected == r";
    public static final String STRING1 = "Boolean a, Boolean r, String e";
    public static final String STRING2 = ":p.contains(uuid)";

    private static PersistenceManagerFactory pmf;

    static {
        pmf = PMF.get();
    }


    
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public static List<User> getAllUsers(final String ordering, final int count) {
         final PersistenceManager pm = pmf.getPersistenceManager();
        List<User> retObj = null;
        try {
            final Query q = pm.newQuery(UserEntity.class);
            q.setOrdering(ordering);
            q.setRange(0, count);
            final Collection<User> result = (Collection<User>) q.execute();
            retObj = UserModelFactory.createUserModels(result);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }
        return retObj;
    }


}
