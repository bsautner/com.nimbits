/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.entity.service;

import com.google.common.base.Optional;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Map;

@Service
public class EntityServiceRpcImpl extends RemoteServiceServlet implements EntityServiceRpc {


    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private ValueService valueService;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public Entity addUpdateEntityRpc(final User user, final Entity entity) throws ClassNotFoundException {

        return entityDao.addUpdateEntity(user, entity);


    }

    @Override
    public void deleteEntityRpc(final User user, final Entity entity) {

        entityService.deleteEntity(user, entity, false);
        if (entity.getEntityType().equals(EntityType.point)) {
            Point point = (Point) entity;

            valueService.deleteAllData(point);


            // taskService.startDeleteDataTask((Point) entity);

        }

    }

    @Override
    public Map<String, Entity> getEntityMapRpc(final User user, final int type, final int limit) {

        return entityDao.getEntityMap(user, EntityType.get(type), limit);
    }

    @Override
    public Entity copyEntity(final User user, final Entity originalEntity, final EntityName newName) {

        switch (originalEntity.getEntityType()) {

            case user:
                return null;
            case point:
                Point p = new PointModel.Builder().init((Point) originalEntity).create();
                p.setName(newName);
                p.setId(null);

                return entityDao.addUpdateEntity(user, p);
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

        return entityService.getEntities(user);
    }

    @Override
    public Entity getEntityByKeyRpc(final User user, final String entityId, final EntityType type) {

        Optional<Entity> entityOptional = entityDao.getEntity(user, entityId, type);
        if (entityOptional.isPresent()) {
            return entityOptional.get();
        } else {
            throw new RuntimeException(String.format("%s %s not found", type.name(), entityId));
        }
    }
}
