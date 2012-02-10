package com.nimbits.server.entity;

import com.nimbits.client.model.user.*;
import com.nimbits.server.dao.entity.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 11:18 AM
 */
public class EntityTransactionFactory {

    public static EntityTransactions getInstance(User user) {
        return new EntityDaoImpl(user);
    }

}
