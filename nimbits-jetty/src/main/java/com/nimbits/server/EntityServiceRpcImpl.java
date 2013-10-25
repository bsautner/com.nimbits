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

package com.nimbits.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.UserHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntityServiceRpcImpl extends RemoteServiceServlet implements EntityServiceRpc {


    NimbitsEngine engine = ApplicationListener.createEngine();
    EntityService service = EntityServiceFactory.getInstance(engine);
    @Override
    public List<Entity> addUpdateEntityRpc(final List<Entity> entity) throws ClassNotFoundException {
        return service.addUpdateEntity(entity);

    }
    @Override
    public List<Entity> deleteEntityRpc(final List<Entity> entityList) {
        User u = UserHelper.getUser(engine);
        if (u != null && ! entityList.isEmpty())  {

            return  service.deleteEntity(u, entityList);
        }
        else {
            return Collections.emptyList();
        }

    }
    @Override
    public Map<String, Entity> getEntityMapRpc(final User user, final int type, final int limit) {
        return service.getEntityModelMap(user, EntityType.get(type), limit);
    }

    @Override
    public List<Entity> copyEntity(final Entity originalEntity, final EntityName newName)  {

        switch (originalEntity.getEntityType()) {

            case user:
                return null;
            case point:
                Point p = PointModelFactory.createPointModel((Point) originalEntity);
                p.setName(newName);
                p.setKey(null);
                List<Entity> list = new ArrayList<Entity>(1);
                list.add(p);
                return service.addUpdateEntity(list);
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
    public List<Entity> getEntitiesRpc(final User user)  {
        List<Entity> response = service.getEntities(user);
        return response;
    }

    @Override
    public List<Entity> getEntityByKeyRpc(final User user, final String entityId, final EntityType type)  {
        return service.getEntityByKey(user, entityId, type);
    }
}
