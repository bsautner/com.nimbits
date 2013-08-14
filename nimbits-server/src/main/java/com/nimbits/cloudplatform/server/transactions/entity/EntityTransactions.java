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

package com.nimbits.cloudplatform.server.transactions.entity;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.user.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 11:46 AM
 */
public interface EntityTransactions  {

    Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) ;

    Map<EntityName, Entity> getEntityNameMap(User user, final EntityType type) ;

    List<Entity> getChildren(final User user, final List<Entity> entity) ;

    Entity addUpdateEntity(final User user, final Entity entity, final boolean clearTree) ;

    Entity addUpdateEntity(final User user, final Entity entity) ;

    List<Entity> getEntities(final User user) ;

    List<Entity> deleteEntity(final User user, final Entity entity, final Class<?> cls) ;

    List<Entity> getEntityByKey(final User user, final String id, final Class<?> cls) ;

    Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) ;

    List<Entity> getEntityByName(final User user, final EntityName name, final Class<?> cls) ;

    List<Entity> getEntityByTrigger(final User user, final Entity entity, final Class<?> cls) ;

    List<Entity> getIdleEntities(User admin) ;

    List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) ;

    void updateUser(final User user) ;


}
