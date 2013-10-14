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

package com.nimbits.cloudplatform.server.transactions.entity.cache;

import com.google.appengine.api.memcache.InvalidValueException;
 
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.subscription.Subscription;
import com.nimbits.cloudplatform.client.model.trigger.Trigger;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.dao.EntityDao;


import javax.jdo.PersistenceManagerFactory;
import java.util.*;
 
public class EntityCacheImpl<T> implements EntityCache {

    private final NimbitsCache cache;
    private final EntityDao entityDao;
    public EntityCacheImpl(PersistenceManagerFactory pmf, NimbitsCache cache) {
        this.cache = cache;
        this.entityDao = EntityServiceFactory.getDaoInstance(pmf);
        
    }

    @Override
    public Map<String, Point> getActivePoints() {

        if (cache.contains(MemCacheKey.activePoints)) {
            try {
                final Map<String, Point> points = (Map<String, Point>) cache.get(MemCacheKey.activePoints);
                cache.delete(MemCacheKey.activePoints); //TODO possible race condition with record value service
                return points == null ? Collections.<String, Point>emptyMap() : points;
            } catch (InvalidValueException e) {
                cache.reloadCache();
                return Collections.emptyMap();
            }

        } else {
            return Collections.emptyMap();
        }

    }

    @Override
    public void removeEntityFromCache(final List<Entity> entities) {
        for (Entity e : entities) {
            cache.delete(e.getKey());
        }


    }


    @Override
    public  List<Entity> getEntityByName(final User user, final EntityName name, final Class<?> cls)  {

            String key = MemCacheKey.entityNameCache.getText() + user.getKey() + name.getValue() + cls.getName();

            if (cache.contains(key)) {
                List<Entity> result = new ArrayList<Entity>(1);
                Entity entity = (Entity) cache.get(key);
                result.add(entity);
                return result;

            } else {
                List<Entity> sample = entityDao.getEntityByName(user, name, cls);
                if (sample.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    cache.put(key, sample.get(0));
                    return sample;
                }

            }



    }

    @Override
    public void removeEntityNameFromCache(final User user, final EntityName name, final String className) {
        try {
            String key = MemCacheKey.entityNameCache.getText() + user.getKey() + name.getValue() + className;
            cache.delete(key);
        } catch (Exception ignored) {

        }
    }


    @Override
    public List<Entity> getEntityByTrigger(final User user, final Entity entity, final Class<?> cls) {
        final String triggerKey = MemCacheKey.triggers + entity.getKey() + cls.getName();
        // final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getClass().getName());
        if (cache.contains(triggerKey)) {
            List<Entity> triggers = (List<Entity>) cache.get(triggerKey);
            if (triggers == null) {
                cache.delete(triggerKey);
                List<Entity> result = entityDao.getEntityByTrigger(user, entity, cls);
                cache.put(triggerKey, result);
                return result;
            } else {
                for (Entity e : triggers) {
                    e.setIsCached(true);
                }
            }
            return triggers;
        } else {
            List<Entity> result = entityDao.getEntityByTrigger(user, entity, cls);
            cache.put(triggerKey, result);
            return result;
        }


    }


    @Override
    public List<Entity> getIdleEntities(User admin) throws Exception {
        return entityDao.getIdleEntities(admin);
    }


    @Override
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) {


        final String key = MemCacheKey.subscribedEntity + user.getKey() + subscribedEntity.getKey();
        final List<Entity> result;
        if (cache.contains(key)) {
            result = (List<Entity>) cache.get(key);
        } else {
            result = entityDao.getSubscriptionsToEntity(user, subscribedEntity);
            cache.put(key, result);
        }

        return result;
    }


    @Override
    public void addEntityToCache(final User user, final List<Entity> entities) {
        removeEntityFromCache(entities);

        for (Entity e : entities) {
            removeEntityNameFromCache(user, e.getName(), e.getEntityType().getClassName());
            cache.put(e.getKey(), e);
        }

    }


    @Override
    public List<Entity> getEntityFromCache(final String key) {
        if (cache.contains(key)) {

            Entity result;
            try {
                result = (Entity) cache.get(key);
            } catch (InvalidValueException ex) {
                cache.delete(key);
                return Collections.emptyList();
            }

            if (result == null) {
                cache.delete(key);
                return Collections.emptyList();
            } else {
                result.setIsCached(true);

                return Arrays.asList(result);
            }
        } else {
            return Collections.emptyList();
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) {
        return entityDao.getEntityMap(user, type, limit);
    }


    @Override
    public List<Entity> getChildren(final User user, final List<Entity> entity)  {
        return entityDao.getChildren(user, entity);
    }


    @Override
    public List<Entity> addUpdateEntity(final User user, final List<Entity> sample, final boolean clearRelatives)   {


        if (! sample.isEmpty()) {
            Entity entity = sample.get(0);
            removeEntityNameFromCache(user, entity.getName(), entity.getEntityType().getClassName());


            final List<Entity> result = entityDao.addUpdateEntity(user, sample);
            addEntityToCache(user, result);
            if (clearRelatives) {
                String treeKey = MemCacheKey.userEntityTree.getText() + user.getKey();
                cache.delete(treeKey);
                removeTriggersFromCache(entity);
            }

            if (entity.getEntityType().equals(EntityType.subscription)) {
                Subscription subscription = (Subscription) entity;
                final String key = MemCacheKey.subscribedEntity + user.getKey() + subscription.getSubscribedEntity();
                cache.delete(key);

            }
            return result;
        }
        else {
            return Collections.emptyList();
        }
    }

    private  void removeTriggersFromCache(Entity entity) {
        if (entity.getEntityType().isTrigger()) {

            Trigger trigger = (Trigger) entity;
            final String triggerKey = MemCacheKey.triggers + trigger.getTrigger() + entity.getEntityType().getClassName();
            cache.delete(triggerKey);
        }
    }


    @Override
    public  List<Entity> getEntities(final User user) {
        List<Entity> results;

        results = entityDao.getEntities(user);

        return results;
    }


    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity, final Class<?> cls) throws Exception {
        String treeKey = MemCacheKey.userEntityTree.getText() + user.getKey();
        removeEntityFromCache(Arrays.asList(entity));
        removeTriggersFromCache(entity);
        removeEntityNameFromCache(user, entity.getName(), entity.getEntityType().getClassName());
        cache.delete(treeKey);
        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            final String key = MemCacheKey.subscribedEntity + user.getKey() + subscription.getSubscribedEntity();
            cache.delete(key);

        }
        return entityDao.deleteEntity(user, entity, cls);
    }


    @Override
    public List<Entity> getEntityByKey(final User user, final String key, final Class<?> cls)   {

        final List<Entity> cached = getEntityFromCache(key);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<Entity> stored = entityDao.getEntityByKey(user, key, cls);

            addEntityToCache(user, stored);
            return stored;
        }

    }
    @Override
    public List<Entity> getEntityByUUID(final User user, final String uuid, final Class<?> cls)   {

        final List<Entity> cached = getEntityFromCache(uuid);
        if (cached != null && !cached.isEmpty() ) {
            return cached;
        } else {
            List<Entity> stored = entityDao.getEntityByUUID(user, uuid, cls);

            addEntityToCache(user, stored);
            return stored;
        }

    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws Exception {
        return entityDao.getSystemWideEntityMap(user, type);
    }


}
