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

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.entity.EntityTransactions;

import java.util.List;
import java.util.Map;

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

    @Override
    public void removeEntityFromCache(Entity entity) throws NimbitsException {
        if (cache.contains(entity.getKey())) {
            cache.delete(entity.getKey());
        }
        if (cache.contains(entity.getName())) {
            cache.delete(entity.getName());
        }

    }
    private void addEntityToCache(Entity entity) throws NimbitsException {
        if (entity != null) {
        removeEntityFromCache(entity);
        cache.put(entity.getName(), entity);
        cache.put(entity.getKey(), entity);
        }
    }

    public EntityCacheImpl(final User u) {
        this.user = u;

        if (user != null) {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.userNamespace.name() + u.getKey().replace('@', '-'));
        } else {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());

        }
    }



    @Override
    public Map<String, Entity> getEntityMap(EntityType type) throws NimbitsException {

        return  EntityTransactionFactory.getDaoInstance(user).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getEntityNameMap(type);
    }

    @Override
    public List<Entity> getChildren(Entity parentEntity, EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getChildren(parentEntity, type);
    }

    @Override
    public Entity addUpdateEntity(Entity entity) throws NimbitsException {

        Entity result =   EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        addEntityToCache(result);
        return result;
    }

    @Override
    public List<Entity> getEntities() throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getEntities();
    }

    @Override
    public List<Entity> deleteEntity(Entity entity) throws NimbitsException {

        removeEntityFromCache(entity);
        return EntityTransactionFactory.getDaoInstance(user).deleteEntity(entity);
    }

    @Override
    public Entity getEntityByKey(String uuid) throws NimbitsException {
        if (cache.contains(uuid)) {

            Entity e =  (Entity) cache.get(uuid);
            if (e != null) {
                return e;
            }
            else {
                Entity result = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(uuid);
                addEntityToCache(result);
                return result;
            }
        }
        else {
            Entity result = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(uuid);

            addEntityToCache(result);
            return result;
        }

    }

//    @Override
//    public Entity getEntityByName(final EntityName name) throws NimbitsException {
//        if (cache.contains(name)) {
//            return (Entity) cache.get(name);
//        }
//        else {
//            final Entity result =  EntityTransactionFactory.getDaoInstance(user).getEntityByName(name);
//            if (result != null) {
//            addEntityToCache(result);
//            }
//            return result;
//        }
//
//    }

    @Override
    public Entity getEntityByName(EntityName name, EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, type);
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(type);
    }
}
