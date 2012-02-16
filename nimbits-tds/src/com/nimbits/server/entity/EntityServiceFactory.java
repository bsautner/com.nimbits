package com.nimbits.server.entity;

import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.server.dao.entity.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 11:18 AM
 */
public class EntityServiceFactory {

    public static EntityService getInstance() {
        return new EntityServiceImpl();
    }

    public static EntityService getDaoInstance(User user) {
        return new EntityDaoImpl(user);
    }

}
