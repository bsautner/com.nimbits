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

package com.nimbits.server.transactions.memcache.entity;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transactions.service.entity.EntityTransactionFactory;
import com.nimbits.server.transactions.service.entity.EntityTransactions;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 11:28 AM
 */
@SuppressWarnings("unchecked")
public class EntityCacheImpl implements EntityTransactions, EntityCache {
    private final User user;
    private final MemcacheService cache;
    public EntityCacheImpl(final User u) {
        this.user = u;

        cache = (user != null && user.getKey() != null)
                ? MemcacheServiceFactory.getMemcacheService(MemCacheKey.getKey(MemCacheKey.userNamespace, user.getKey()))
                : MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());
    }

    @Override
    public void removeEntityFromCache(final List<Entity> entities) throws NimbitsException {
        for (Entity e : entities) {
            cache.delete(e.getKey());
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
    public List<Entity> getEntityByTrigger(final Entity entity, final Class<?> cls) throws NimbitsException {
        final String triggerKey =MemCacheKey.getKey(MemCacheKey.triggers, entity.getKey() + cls.getName());
        // final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getClass().getName());
        if (cache.contains(triggerKey)) {
            List<Entity> triggers =  (List<Entity>) cache.get(triggerKey);
            if (triggers == null) {
                cache.delete(triggerKey);
                List<Entity> result= EntityTransactionFactory.getDaoInstance(user).getEntityByTrigger(entity, cls);
                cache.put(triggerKey, result);
                return result;
            }
            else {
                for (Entity e : triggers) {
                    e.setIsCached(true);
                }
            }
            return triggers;
        }
        else {
            List<Entity> result= EntityTransactionFactory.getDaoInstance(user).getEntityByTrigger(entity, cls);
            cache.put(triggerKey, result);
            return result;
        }


    }

    @Override
    public List<Entity> getIdleEntities() throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getIdleEntities();
    }

    @Override
    public List<Entity> getSubscriptionsToEntity(final Entity subscribedEntity) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getSubscriptionsToEntity(subscribedEntity);
    }

    @Override
    public List<Entity> getEntityByBlobKey(final BlobKey key) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntityByBlobKey(key);
    }

    @Override
    public void addEntityToCache(final  List<Entity> entities) throws NimbitsException {
        removeEntityFromCache(entities);
        for (Entity e : entities) {

            cache.put(e.getKey(), e);
        }

    }

    @Override
    public List<Entity> getEntityFromCache(String key) throws NimbitsException {
        if (cache.contains(key)) {
            List<Entity> list = new ArrayList<Entity>(1);
            Entity result = (Entity) cache.get(key);
            result.setIsCached(true);
            list.add(result);
            return list;
        }
        else {
            return Collections.emptyList();
        }
        //To change body of implemented methods use File | Settings | File Templates.
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
        addEntityToCache(Arrays.asList(result));
        cache.delete(MemCacheKey.userEntityTree);
        removeTriggersFromCache(entity);
        return result;
    }

    private void removeTriggersFromCache(Entity entity) {
        if (entity.getEntityType().isTrigger()) {
            Trigger trigger = (Trigger)entity;
            final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getEntityType().getClassName());
            cache.delete(triggerKey);
        }
    }

    @Override
    public List<Entity> getEntities() throws NimbitsException {
        List<Entity> results;
        if (cache.contains(MemCacheKey.userEntityTree)) {
            results= (List<Entity>) cache.get(MemCacheKey.userEntityTree);
            for (Entity e: results) {
                e.setIsCached(true);
            }

        }
        else {
            results = EntityTransactionFactory.getDaoInstance(user).getEntities();
            cache.put(MemCacheKey.userEntityTree, results);

        }
        return results;
    }

    @Override
    public List<Entity> deleteEntity(final Entity entity, final Class<?> cls) throws NimbitsException {
        removeEntityFromCache(Arrays.asList(entity));
        removeTriggersFromCache(entity);
        cache.delete(MemCacheKey.userEntityTree);
        return EntityTransactionFactory.getDaoInstance(user).deleteEntity(entity, cls);
    }

    @Override
    public List<Entity> getEntityByKey(final String key, final Class<?> cls) throws NimbitsException {

        List<Entity> cached = getEntityFromCache(key);
        if (! cached.isEmpty()) {
            return cached;
        }
        else {
            List<Entity> stored = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(key, cls);
            addEntityToCache(stored);
            return stored;
        }

    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(type);
    }

}
