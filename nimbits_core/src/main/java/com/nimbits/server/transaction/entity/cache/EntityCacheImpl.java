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

import com.google.appengine.api.memcache.InvalidValueException;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.dao.EntityDao;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntityCacheImpl<T> implements EntityCache {

    private static final String ENTITY_LIST = "ENTITY_LIST";


    private final NimbitsCache cache;
    private final EntityDao entityDao;

    public EntityCacheImpl(NimbitsEngine engine) {
        this.cache = engine.getCache();
        this.entityDao = EntityServiceFactory.getDaoInstance(engine);

    }

    private void clearCache(final User user) {
        String key = ENTITY_LIST + user.getEmail().getValue();
        cache.remove(key);
    }


    private void createCache(final User user, final List<Entity> entities) {

        String key = ENTITY_LIST + user.getEmail().getValue();
        cache.put(key, entities);


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
    public  List<Entity> getEntityByName(final User user, final EntityName name, final Class<?> cls)  {


        List<Entity> sample = entityDao.getEntityByName(user, name, cls);
        return sample;



    }


    @Override
    public List<Entity> getEntityByTrigger(final User user, final Entity trigger, final Class<?> cls) {


        List<Entity> result = entityDao.getEntityByTrigger(user, trigger, cls);

        return result;

    }


    @Override
    public List<Entity> getIdleEntities(User admin) {
        return entityDao.getIdleEntities(admin);
    }


    @Override
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) {


        final List<Entity> result;
        result = entityDao.getSubscriptionsToEntity(user, subscribedEntity);

        return result;
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
    public List<Entity> addUpdateEntity(final User user, final List<Entity> sample)   {

        clearCache(user);
        if (! sample.isEmpty()) {

            final List<Entity> result = entityDao.addUpdateEntity(user, sample);

            return result;
        }
        else {
            return sample;
        }
    }


    @Override
    public  List<Entity> getEntities(final User user) {
        List<Entity> results;
        String key = ENTITY_LIST + user.getEmail().getValue();
        if (cache.containsKey(key)) {
            results = (List<Entity>) cache.get(key);

        }
        else {
            results = entityDao.getEntities(user);
            createCache(user, results);

        }

        return results;
    }


    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity, final Class<?> cls)  {


        clearCache(user);
        return entityDao.deleteEntity(user, entity, cls);
    }


    @Override
    public List<Entity> getEntityByKey(final User user, final String key, final Class<?> cls)   {


        List<Entity> stored = entityDao.getEntityByKey(user, key, cls);

        return stored;
    }

    @Override
    public List<Entity> getEntityByUUID(final User user, final String uuid, final Class<?> cls)   {

            List<Entity> stored = entityDao.getEntityByUUID(user, uuid, cls);

            return stored;


    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type)  {
        return entityDao.getSystemWideEntityMap(user, type);
    }


}
