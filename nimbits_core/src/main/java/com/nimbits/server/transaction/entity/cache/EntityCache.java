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

package com.nimbits.server.transaction.entity.cache;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;

import java.util.List;
import java.util.Map;

/**
 * Created by benjamin on 10/14/13.
 */
public interface EntityCache {
    Map<String, Point> getActivePoints();

    List<Entity> getEntityByName(User user, EntityName name, Class<?> cls);

    List<Entity> getEntityByTrigger(User user, Entity trigger, Class<?> cls);

    List<Entity> getIdleEntities(User admin);

    List<Entity> getSubscriptionsToEntity(User user, Entity subscribedEntity);

    Map<String, Entity> getEntityMap(User user, EntityType type, int limit);

    List<Entity> getChildren(User user, List<Entity> entity);

    List<Entity> addUpdateEntity(User user, List<Entity> sample);

    List<Entity> getEntities(User user);

    List<Entity> deleteEntity(User user, Entity entity, Class<?> cls);

    List<Entity> getEntityByKey(User user, String key, Class<?> cls);

    List<Entity> getEntityByUUID(User user, String uuid, Class<?> cls);

    Map<String, Entity> getSystemWideEntityMap(User user, EntityType type);
}
