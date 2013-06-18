/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL protected static LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.transactions.entity;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.subscription.Subscription;
import com.nimbits.cloudplatform.client.model.trigger.Trigger;
import com.nimbits.cloudplatform.client.model.user.User;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 11:28 AM
 */

@Component("entityCache")
public class EntityCache<T> {

    private static MemcacheService cacheFactory;

    static {
        cacheFactory = MemcacheServiceFactory.getMemcacheService();
    }

    protected static Map<String, Point> getActivePoints() {

        if (cacheFactory.contains(MemCacheKey.activePoints)) {
            try {
                final Map<String, Point> points = (Map<String, Point>) cacheFactory.get(MemCacheKey.activePoints);
                cacheFactory.delete(MemCacheKey.activePoints); //TODO possible race condition with record value service
                return points == null ? Collections.<String, Point>emptyMap() : points;
            } catch (InvalidValueException e) {
                cacheFactory.clearAll();
                return Collections.emptyMap();
            }

        } else {
            return Collections.emptyMap();
        }

    }

    protected static void removeEntityFromCache(final List<Entity> entities) {
        for (Entity e : entities) {
            cacheFactory.delete(e.getKey());
        }


    }


    protected static List<Entity> getEntityByName(final User user, final EntityName name, final Class<?> cls) throws Exception {
        try {
            String key = MemCacheKey.entityNameCache.getText() + user.getKey() + name.getValue() + cls.getName();

            if (cacheFactory.contains(key)) {
                List<Entity> result = new ArrayList<Entity>(1);
                Entity entity = (Entity) cacheFactory.get(key);
                result.add(entity);
                return result;

            } else {
                List<Entity> sample = EntityDao.getEntityByName(user, name, cls);
                if (sample.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    cacheFactory.put(key, sample.get(0));
                    return sample;
                }

            }
        } catch (NullPointerException e) {
            throw new Exception(e);


        }


    }

    protected static void removeEntityNameFromCache(final User user, final EntityName name, final String className) {
        try {
            String key = MemCacheKey.entityNameCache.getText() + user.getKey() + name.getValue() + className;
            cacheFactory.delete(key);
        } catch (Exception ignored) {

        }
    }


    protected static List<Entity> getEntityByTrigger(final User user, final Entity entity, final Class<?> cls) {
        final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, entity.getKey() + cls.getName());
        // final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getClass().getName());
        if (cacheFactory.contains(triggerKey)) {
            List<Entity> triggers = (List<Entity>) cacheFactory.get(triggerKey);
            if (triggers == null) {
                cacheFactory.delete(triggerKey);
                List<Entity> result = EntityDao.getEntityByTrigger(user, entity, cls);
                cacheFactory.put(triggerKey, result);
                return result;
            } else {
                for (Entity e : triggers) {
                    e.setIsCached(true);
                }
            }
            return triggers;
        } else {
            List<Entity> result = EntityDao.getEntityByTrigger(user, entity, cls);
            cacheFactory.put(triggerKey, result);
            return result;
        }


    }


    protected static List<Entity> getIdleEntities(User admin) throws Exception {
        return EntityDao.getIdleEntities(admin);
    }


    protected static List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) {


        final String key = MemCacheKey.subscribedEntity + user.getKey() + subscribedEntity.getKey();
        final List<Entity> result;
        if (cacheFactory.contains(key)) {
            result = (List<Entity>) cacheFactory.get(key);
        } else {
            result = EntityDao.getSubscriptionsToEntity(user, subscribedEntity);
            cacheFactory.put(key, result);
        }

        return result;
    }


    protected static void addEntityToCache(final User user, final List<Entity> entities) {
        removeEntityFromCache(entities);

        for (Entity e : entities) {
            removeEntityNameFromCache(user, e.getName(), e.getEntityType().getClassName());
            cacheFactory.put(e.getKey(), e);
        }

    }


    protected static List<Entity> getEntityFromCache(final String key) {
        if (cacheFactory.contains(key)) {

            Entity result;
            try {
                result = (Entity) cacheFactory.get(key);
            } catch (InvalidValueException ex) {
                cacheFactory.delete(key);
                return Collections.emptyList();
            }

            if (result == null) {
                cacheFactory.delete(key);
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


    protected static Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) {
        return EntityDao.getEntityMap(user, type, limit);
    }


    protected static List<Entity> getChildren(final User user, final List<Entity> entity) throws Exception {
        return EntityDao.getChildren(user, entity);
    }


    protected static List<Entity> addUpdateEntity(final User user, final List<Entity> sample, final boolean clearRelatives)   {


        if (! sample.isEmpty()) {
            Entity entity = sample.get(0);
            removeEntityNameFromCache(user, entity.getName(), entity.getEntityType().getClassName());


            final List<Entity> result = EntityDao.addUpdateEntity(user, sample);
            addEntityToCache(user, result);
            if (clearRelatives) {
                String treeKey = MemCacheKey.userEntityTree.getText() + user.getKey();
                cacheFactory.delete(treeKey);
                removeTriggersFromCache(entity);
            }

            if (entity.getEntityType().equals(EntityType.subscription)) {
                Subscription subscription = (Subscription) entity;
                final String key = MemCacheKey.subscribedEntity + user.getKey() + subscription.getSubscribedEntity();
                cacheFactory.delete(key);

            }
            return result;
        }
        else {
            return Collections.emptyList();
        }
    }

    private static void removeTriggersFromCache(Entity entity) {
        if (entity.getEntityType().isTrigger()) {

            Trigger trigger = (Trigger) entity;
            final String triggerKey = MemCacheKey.getKey(MemCacheKey.triggers, trigger.getTrigger() + entity.getEntityType().getClassName());
            cacheFactory.delete(triggerKey);
        }
    }


    protected static List<Entity> getEntities(final User user) {
        List<Entity> results;
        //String treeKey = MemCacheKey.userEntityTree.getText() + user.getKey();
//
//      //  if (cacheFactory.contains(treeKey)) {
//            results= (List<Entity>) cacheFactory.get(treeKey);
//            for (Entity e: results) {
//                e.setIsCached(true);
//            }
//
//        }
//        else {
        results = EntityDao.getEntities(user);
        // cacheFactory.put(treeKey, results);

        /// }
        return results;
    }


    protected static List<Entity> deleteEntity(final User user, final Entity entity, final Class<?> cls) throws Exception {
        String treeKey = MemCacheKey.userEntityTree.getText() + user.getKey();
        removeEntityFromCache(Arrays.asList(entity));
        removeTriggersFromCache(entity);
        removeEntityNameFromCache(user, entity.getName(), entity.getEntityType().getClassName());
        cacheFactory.delete(treeKey);
        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            final String key = MemCacheKey.subscribedEntity + user.getKey() + subscription.getSubscribedEntity();
            cacheFactory.delete(key);

        }
        return EntityDao.deleteEntity(user, entity, cls);
    }


    protected static List<Entity> getEntityByKey(final User user, final String key, final Class<?> cls)   {

        final List<Entity> cached = getEntityFromCache(key);
        if (!cached.isEmpty()) {
            return cached;
        } else {
            List<Entity> stored = EntityDao.getEntityByKey(user, key, cls);

            addEntityToCache(user, stored);
            return stored;
        }

    }


    protected static Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws Exception {
        return EntityDao.getSystemWideEntityMap(user, type);
    }


}
