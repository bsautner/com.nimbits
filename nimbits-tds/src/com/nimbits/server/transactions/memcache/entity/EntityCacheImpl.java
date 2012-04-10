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

import com.google.appengine.api.memcache.InvalidValueException;
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
import com.nimbits.server.logging.LogHelper;

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
    public void removeEntityFromCache(final Entity entity) throws NimbitsException {
        if (cache.contains(entity.getKey())) {
            cache.delete(entity.getKey());
        }

    }

    @Override
    public Entity getEntityByName(final EntityName name, final Class<?> cls) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, cls);
    }

    private void addEntityToCache(final Entity entity) throws NimbitsException {
        if (entity != null) {
        removeEntityFromCache(entity);
        cache.put(entity.getKey(), entity);
        }
    }

    public EntityCacheImpl(final User u) {
        this.user = u;
        LogHelper.log(this.getClass(), "EntityCacheImpl user is null?" + (u == null));

        cache = user != null
                ? MemcacheServiceFactory.getMemcacheService(MemCacheKey.userNamespace.name() + user.getKey().replace('@', '-'))
                : MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());
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
    public Entity getEntityByKey(final String uuid, final Class<?> cls) throws NimbitsException {
        if (cache.contains(uuid)) {

            try {
                final Entity e =  (Entity) cache.get(uuid);
                return e != null ? e : getEntityFromStore(uuid, cls);
            } catch (InvalidValueException e1) {
                return getEntityFromStore(uuid, cls);
            }
        }
        else {
            return getEntityFromStore(uuid, cls);
        }

    }

    private Entity getEntityFromStore(final String key, final Class<?> cls) throws NimbitsException {
        final Entity result = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(key, cls);
        addEntityToCache(result);
        return result;
    }

    @Override
    public Entity getEntityByName(final EntityName name, final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, type);
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type, final Class<?> cls) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(type, cls);
    }

}
