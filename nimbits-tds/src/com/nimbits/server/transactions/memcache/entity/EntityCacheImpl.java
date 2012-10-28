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
import com.google.appengine.api.memcache.MemcacheService;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transactions.service.entity.EntityTransactions;
import com.nimbits.server.transactions.service.user.UserServiceImpl;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 11:28 AM
 */
@SuppressWarnings("unchecked")
@Component("entityCache")
public class EntityCacheImpl implements EntityTransactions,  EntityCache {

    //private final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    private EntityTransactions entityDao;
    private UserServiceImpl userService;
    private MemcacheService cacheFactory;


    @Override
    public void removeEntityFromCache(final User user, final List<Entity> entities) throws NimbitsException {
        for (Entity e : entities) {
            cacheFactory.delete(e.getKey());
        }


    }

    @Override
    public List<Entity> getEntityByName(final User user, final EntityName name, final Class<?> cls) throws NimbitsException {
        String key = MemCacheKey.entityNameCache.getText() + user.getKey() + name.getValue() + cls.getName();

        if (cacheFactory.contains(key)) {
            List<Entity> result = new ArrayList<Entity>(1);
            Entity entity = (Entity) cacheFactory.get(key);
            result.add(entity);
            return result;

        }
        else {
            List<Entity> sample = entityDao.getEntityByName(user, name, cls);
            if (sample.isEmpty()) {
                return Collections.emptyList();
            }
            else {
                cacheFactory.put(key, sample.get(0));
                return sample;
            }

        }


    }
    public void removeEntityNameFromCache(final User user, final EntityName name, final String className) {
        try {
            String key = MemCacheKey.entityNameCache.getText() + user.getKey() + name.getValue() + className;
            cacheFactory.delete(key);
        } catch (Exception ignored) {

        }
    }
    @Override
    public List<Entity> getEntitiesBySource(final User user, final Entity source, final Class<?> cls) throws NimbitsException {
        return entityDao.getEntitiesBySource(user, source, cls);
    }

    @Override
    public List<Entity> getEntityByTrigger(final User user, final Entity entity, final Class<?> cls) throws NimbitsException {
        final String triggerKey =MemCacheKey.getKey(MemCacheKey.triggers, entity.getKey() + cls.getName());
        // final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getClass().getName());
        if (cacheFactory.contains(triggerKey)) {
            List<Entity> triggers =  (List<Entity>) cacheFactory.get(triggerKey);
            if (triggers == null) {
                cacheFactory.delete(triggerKey);
                List<Entity> result= entityDao.getEntityByTrigger(user, entity, cls);
                cacheFactory.put(triggerKey, result);
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
            List<Entity> result= entityDao.getEntityByTrigger(user, entity, cls);
            cacheFactory.put(triggerKey, result);
            return result;
        }


    }

    @Override
    public List<Entity> getIdleEntities(User admin) throws NimbitsException {
        return entityDao.getIdleEntities(userService.getAdmin());
    }

    @Override
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) throws NimbitsException {


        final String key = MemCacheKey.subscribedEntity + user.getKey() + subscribedEntity.getKey();
        final List<Entity> result;
        if (cacheFactory.contains(key)) {
            result = (List<Entity>) cacheFactory.get(key);
        }
        else {
            result = entityDao.getSubscriptionsToEntity(user, subscribedEntity);
            cacheFactory.put(key, result);
        }

        return result;
    }



    @Override
    public List<Entity> getEntityByBlobKey(final User user, final BlobKey key) throws NimbitsException {
        return entityDao.getEntityByBlobKey(user, key);
    }

    @Override
    public void updateUser(User user) throws NimbitsException {
        if (cacheFactory.contains(user.getKey())) {
            cacheFactory.delete(user.getKey());
            removeEntityNameFromCache(user, user.getName(), user.getEntityType().getClassName());
        }

        else {

            cacheFactory.put(user.getKey(), user);
        }
    }


    @Override
    public void addEntityToCache(final User user, final  List<Entity> entities) throws NimbitsException {
        removeEntityFromCache(user, entities);

        for (Entity e : entities) {
            removeEntityNameFromCache(user, e.getName(), e.getEntityType().getClassName());
            cacheFactory.put(e.getKey(), e);
        }

    }

    @Override
    public List<Entity> getEntityFromCache(final User user, final String key) throws NimbitsException {
        if (cacheFactory.contains(key)) {
            List<Entity> list = new ArrayList<Entity>(1);
            Entity result = (Entity) cacheFactory.get(key);
            if (result == null) {
                cacheFactory.delete(key);
                return Collections.emptyList();
            }
            else {
                result.setIsCached(true);
                list.add(result);
                return list;
            }
        }
        else {
            return Collections.emptyList();
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) throws NimbitsException {
        return  entityDao.getEntityMap(user, type, limit);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(User user, final EntityType type) throws NimbitsException {
        return  entityDao.getEntityNameMap(user, type);
    }

    @Override
    public List<Entity> getChildren(final User user, Entity entity, final EntityType type) throws NimbitsException {
        return  entityDao.getChildren(user, entity, type);
    }

    @Override
    public Entity addUpdateEntity(final User user, final Entity entity, final boolean clearRelatives) throws NimbitsException {
        removeEntityNameFromCache(user, entity.getName(), entity.getEntityType().getClassName());
        final Entity result =   entityDao.addUpdateEntity(user, entity);
        addEntityToCache(user, Arrays.asList(result));
        if (clearRelatives) {
            cacheFactory.delete(MemCacheKey.userEntityTree);
            removeTriggersFromCache(entity);
        }

        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            final String key = MemCacheKey.subscribedEntity + user.getKey() + subscription.getSubscribedEntity();
            cacheFactory.delete(key);

        }
        return result;
    }
    @Override
    public Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }
    private void removeTriggersFromCache(Entity entity) {
        if (entity.getEntityType().isTrigger()) {

            Trigger trigger = (Trigger)entity;
            final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getEntityType().getClassName());
            cacheFactory.delete(triggerKey);
        }
    }

    @Override
    public List<Entity> getEntities(final User user) throws NimbitsException {
        List<Entity> results;
        if (cacheFactory.contains(MemCacheKey.userEntityTree)) {
            results= (List<Entity>) cacheFactory.get(MemCacheKey.userEntityTree);
            for (Entity e: results) {
                e.setIsCached(true);
            }

        }
        else {
            results = entityDao.getEntities(user);
            cacheFactory.put(MemCacheKey.userEntityTree, results);

        }
        return results;
    }

    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity, final Class<?> cls) throws NimbitsException {
        removeEntityFromCache(user, Arrays.asList(entity));
        removeTriggersFromCache(entity);
        removeEntityNameFromCache(user, entity.getName(), entity.getEntityType().getClassName());
        cacheFactory.delete(MemCacheKey.userEntityTree);
        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            final String key = MemCacheKey.subscribedEntity + user.getKey() + subscription.getSubscribedEntity();
            cacheFactory.delete(key);

        }
        return entityDao.deleteEntity(user, entity, cls);
    }

    @Override
    public List<Entity> getEntityByKey(final User user, final String key, final Class<?> cls) throws NimbitsException {

        final List<Entity> cached = getEntityFromCache(user, key);
        if (! cached.isEmpty()) {
            return cached;
        }
        else {
            List<Entity> stored = entityDao.getEntityByKey(user, key, cls);

            addEntityToCache(user, stored);
            return stored;
        }

    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws NimbitsException {
        return  entityDao.getSystemWideEntityMap(userService.getAdmin(), type);
    }

    public void setEntityDao(EntityTransactions entityDao) {
        this.entityDao = entityDao;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public void setCacheFactory(MemcacheService cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

}
