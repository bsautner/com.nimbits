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

package com.nimbits.server.transactions;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class EntityServiceRpcImpl extends RemoteServiceServlet implements EntityServiceRpc {
    final static Logger logger = Logger.getLogger(EntityServiceRpcImpl.class.getName());

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserHelper userHelper;

    @Override
    public List<Entity> addUpdateEntityRpc(final List<Entity> entity) throws ClassNotFoundException {
        return entityService.addUpdateEntity(entity);

    }

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }


    @Override
    public List<Entity> deleteEntityRpc(final List<Entity> entityList) {
        User u = userHelper.getUser().get(0);
        if (u != null && !entityList.isEmpty()) {

            return entityService.deleteEntity(u, entityList);
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public Map<String, Entity> getEntityMapRpc(final User user, final int type, final int limit) {
        return entityService.getEntityModelMap(user, EntityType.get(type), limit);
    }

    @Override
    public List<Entity> copyEntity(final Entity originalEntity, final EntityName newName) {

        switch (originalEntity.getEntityType()) {

            case user:
                return null;
            case point:
                Point p = PointModelFactory.createPointModel((Point) originalEntity);
                p.setName(newName);
                p.setKey(null);
                List<Entity> list = new ArrayList<Entity>(1);
                list.add(p);
                return entityService.addUpdateEntity(list);
            //return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);

            case category:
                return null;

            case subscription:
                return null;
            default:
                return null;
        }
    }

    @Override
    public List<Entity> getEntitiesRpc(final User user) {

        List<Entity> result = entityService.getEntities(user);
        logger.info("getEntitiesRpc:: " + result.size());
        return result;
    }

    @Override
    public List<Entity> getEntityByKeyRpc(final User user, final String entityId, final EntityType type) {
        return entityService.getEntityByKey(user, entityId, type);
    }
}
