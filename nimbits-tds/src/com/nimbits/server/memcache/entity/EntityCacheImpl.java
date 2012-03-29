package com.nimbits.server.memcache.entity;

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
        if (cache.contains(entity.getEntity())) {
            cache.delete(entity.getEntity());
        }
        if (cache.contains(entity.getName())) {
            cache.delete(entity.getName());
        }

    }
    private void addEntityToCache(Entity entity) throws NimbitsException {
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
    public Map<String, Entity> getEntityMap(EntityType type) throws NimbitsException {

        return  EntityTransactionFactory.getDaoInstance(user).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getEntityNameMap(type);
    }

    @Override
    public List<Entity> getChildren(Entity parentEntity, EntityType type) {
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
    public void deleteEntity(Entity entity) throws NimbitsException {
        EntityTransactionFactory.getDaoInstance(user).deleteEntity(entity);
        removeEntityFromCache(entity);
    }

    @Override
    public Entity getEntityByUUID(String uuid) throws NimbitsException {
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
    public Entity getEntityByName(EntityName name) throws NimbitsException {
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
    public Map<String, Entity> getSystemWideEntityMap(EntityType type) throws NimbitsException {
        return  EntityTransactionFactory.getDaoInstance(user).getSystemWideEntityMap(type);
    }
}
