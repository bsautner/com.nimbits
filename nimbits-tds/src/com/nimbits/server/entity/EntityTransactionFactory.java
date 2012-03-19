package com.nimbits.server.entity;

import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.server.dao.entity.*;
import com.nimbits.server.memcache.entity.*;

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
