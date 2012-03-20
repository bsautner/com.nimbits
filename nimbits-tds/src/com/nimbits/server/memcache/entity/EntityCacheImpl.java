package com.nimbits.server.memcache.entity;

import com.google.appengine.api.memcache.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.memcache.*;

import javax.persistence.*;
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

    private void removeEntityFromCache(Entity entity) {
        if (cache.contains(entity.getEntity())) {
            cache.delete(entity.getEntity());
        }
        if (cache.contains(entity.getName())) {
            cache.delete(entity.getName());
        }

    }
    private void addEntityToCache(Entity entity) {
        removeEntityFromCache(entity);
        cache.put(entity.getName(), entity);
        cache.put(entity.getEntity(), entity);
    }

    public EntityCacheImpl(final User u) {
        this.user = u;

        if (user != null) {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.userNamespace.name() + u.getUuid());
        } else {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());

        }
    }



    @Override
    public Map<String, Entity> getEntityMap(EntityType type) {

        return  EntityTransactionFactory.getDaoInstance(user).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) {
        return  EntityTransactionFactory.getDaoInstance(user).getEntityNameMap(type);
    }

    @Override
    public List<Entity> getEntityChildren(Entity parentEntity, EntityType type) {
        return  EntityTransactionFactory.getDaoInstance(user).getEntityChildren(parentEntity, type);
    }

    @Override
    public Entity addUpdateEntity(Entity entity) throws NimbitsException {

        Entity result =   EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        addEntityToCache(result);
        return result;
    }

    @Override
    public List<Entity> getEntities() {
        return  EntityTransactionFactory.getDaoInstance(user).getEntities();
    }

    @Override
    public void deleteEntity(Entity entity) {
        EntityTransactionFactory.getDaoInstance(user).deleteEntity(entity);
        removeEntityFromCache(entity);
    }

    @Override
    public Entity getEntityByUUID(String uuid) {
        if (cache.contains(uuid)) {
            return (Entity) cache.get(uuid);
        }
        else {
            Entity result = EntityTransactionFactory.getDaoInstance(user).getEntityByUUID(uuid);
            addEntityToCache(result);
            return result;
        }

    }

    @Override
    public Entity getEntityByName(EntityName name) {
        if (cache.contains(name)) {
            return (Entity) cache.get(name);
        }
        else {
            Entity result =  EntityTransactionFactory.getDaoInstance(user).getEntityByName(name);
            if (result != null) {
            addEntityToCache(result);
            }
            return result;
        }

    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(EntityType type) {
        return  EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(type);
    }
}
