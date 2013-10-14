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

package com.nimbits.cloudplatform.server.transactions.entity.service;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;

import java.util.List;
import java.util.Map;

/**
 * Created by benjamin on 10/14/13.
 */
public interface EntityService {
    Map<String, Point> getActivePoints();

    List<Entity> deleteEntity(User user, List<Entity> entities);

    List<Entity> getEntities(User user);

    Entity addUpdateSingleEntity(Entity entity);

    List<Entity> addUpdateEntity(List<Entity> list);

    List<Entity> findEntityByKey(User user, String key);

    Map<String, Entity> getEntityModelMap(User user, EntityType type, int limit);

    List<Entity> getChildren(User user, List<Entity> parentEntity);

    List<Entity> getEntityByName(User user, EntityName name, EntityType type);

    List<Entity> getEntityByTrigger(User user, Entity trigger, EntityType type);

    Map<String, Entity> getSystemWideEntityMap(User user, EntityType type) throws Exception;

    List<Entity> getIdleEntities() throws Exception;

    List<Entity> getSubscriptionsToEntity(User user, Entity subscribedEntity);

    List<Entity> addUpdateEntity(User user, Entity entity);

    List<Entity> addUpdateEntity(User user, List<Entity> entity);

    List<Entity> getEntityByKey(User user, String entityId, EntityType type);

    List<Entity> getEntityByUUID(User user, String uuid, EntityType type);
}
