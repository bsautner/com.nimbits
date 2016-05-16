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

package com.nimbits.server.transaction.entity.dao;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.user.User;

import java.util.List;
import java.util.Map;


public interface EntityDao {


    List<Entity> getSubscriptionsToEntity(User user, Entity subscribedEntity);

    List<Entity> getIdleEntities(User admin);

    Optional<Entity> getEntityByTrigger(User user, Entity entity, EntityType type);

    Map<String, Entity> getEntityMap(User user, EntityType type, int limit);

    List<Entity> getChildren(User user, List<Entity> parents);

    Entity addUpdateEntity(User user, Entity  entity);

    List<Entity> getEntities(User user);

    List<Entity> getEntitiesByType(User user, EntityType type);

    void deleteEntity(User user, Entity entity, EntityType type);

    Optional<Entity> getEntity(User user, String id, EntityType type);

    Optional<User> getUser(String email);

    Optional<Entity> getEntityByName(User user, EntityName name, EntityType type);

    List<Connection> approveConnection(String id);

    String getOwner(String point);

    List<Schedule> getSchedules();

    Optional<Entity> findEntity(User user, String uuid);

}
