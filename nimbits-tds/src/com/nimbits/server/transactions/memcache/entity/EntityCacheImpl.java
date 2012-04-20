/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.transactions.memcache.entity;

import com.google.appengine.api.memcache.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.transactions.service.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 11:28 AM
 */
@SuppressWarnings("unchecked")
public class EntityCacheImpl implements EntityTransactions {
    private final User user;
    private final MemcacheService cache;
    public EntityCacheImpl(final User u) {
        this.user = u;

        cache = user != null && user.getKey() != null
                ? MemcacheServiceFactory.getMemcacheService(MemCacheKey.userNamespace.name() + user.getKey().replace('@', '-'))
                : MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());
    }

    @Override
    public void removeEntityFromCache(final Entity entity) throws NimbitsException {
        if (cache.contains(entity.getKey())) {
            cache.delete(entity.getKey());
        }

    }

    @Override
    public List<Entity> getEntityByName(final EntityName name, final Class<?> cls) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, cls);
    }

    @Override
    public List<Entity> getEntitiesBySource(final Entity source, final Class<?> cls) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntitiesBySource(source, cls);
    }

    @Override
    public List<Entity> getEntityByTrigger(Entity entity, Class<?> cls) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntityByTrigger(entity, cls);
    }

    @Override
    public List<Entity> getIdleEntities() throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getIdleEntities();
    }

    @Override
    public List<Entity> getSubscriptionsToEntity(Entity subscribedEntity) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getSubscriptionsToEntity(subscribedEntity);
    }

    private void addEntityToCache(final Entity entity) throws NimbitsException {
        if (entity != null) {
            removeEntityFromCache(entity);
            cache.put(entity.getKey(), entity);
        }
    }



    @Override
    public Map<String, Entity> getEntityMap(final EntityType type, final int limit) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getEntityMap(type, limit);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getEntityNameMap(type);
    }

    @Override
    public List<Entity> getChildren(final Entity parentEntity, final EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getChildren(parentEntity, type);
    }

    @Override
    public Entity addUpdateEntity(final Entity entity) throws NimbitsException {
        final Entity result =   EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        addEntityToCache(result);
        return result;
    }

    @Override
    public List<Entity> getEntities() throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getEntities();
    }

    @Override
    public List<Entity> deleteEntity(final Entity entity, final Class<?> cls) throws NimbitsException {
        removeEntityFromCache(entity);
        return EntityTransactionFactory.getDaoInstance(user).deleteEntity(entity, cls);
    }

    @Override
    public List<Entity> getEntityByKey(final String key, final Class<?> cls) throws NimbitsException {
        if (cache.contains(key)) {
            try {
                final Entity e =  (Entity) cache.get(key);
                return e != null ? Arrays.asList(e) : getEntityFromStore(key, cls);
            } catch (InvalidValueException e1) {
                return getEntityFromStore(key, cls);
            }
        }
        else {
            return getEntityFromStore(key, cls);

        }

    }

    private  List<Entity> getEntityFromStore(final String key, final Class<?> cls) throws NimbitsException {
        final List<Entity> result = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(key, cls);
        if (! result.isEmpty()) {
            addEntityToCache(result.get(0));
        }
        return result;
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(type);
    }

}
