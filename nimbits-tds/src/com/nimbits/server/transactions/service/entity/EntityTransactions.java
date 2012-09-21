/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.service.entity;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transactions.memcache.entity.EntityCache;

import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 11:46 AM
 */
public interface EntityTransactions extends EntityCache {

    Map<String, Entity> getEntityMap(final User user,  final EntityType type, final int limit) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(User user, final EntityType type) throws NimbitsException;

    List<Entity> getChildren(final User user, Entity entity, final EntityType type) throws NimbitsException;

    Entity addUpdateEntity( final User user, final Entity entity, final boolean clearTree) throws NimbitsException;

    Entity addUpdateEntity(final User user,  final Entity entity) throws NimbitsException;

    List<Entity> getEntities(final User user) throws NimbitsException;

    List<Entity> deleteEntity(final User user,  final Entity entity,final Class<?> cls) throws NimbitsException;

    List<Entity> getEntityByKey(final User user,  final String id,  final Class<?> cls) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws NimbitsException;

    List<Entity> getEntityByName( final User user, final EntityName name,  final Class<?> cls) throws NimbitsException;

    List<Entity> getEntitiesBySource(final User user, final Entity source, final Class<?> cls) throws NimbitsException;

    List<Entity> getEntityByTrigger(final User user, final Entity entity, final Class<?> cls) throws NimbitsException;

    List<Entity> getIdleEntities(User admin) throws NimbitsException;

    List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) throws NimbitsException;

    List<Entity> getEntityByBlobKey(final User user, final BlobKey key) throws NimbitsException;

    void updateUser(final User user) throws NimbitsException;


}
