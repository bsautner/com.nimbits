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

package com.nimbits.cloudplatform.server.transactions.entity;

import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.entity.cache.EntityCache;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.search.EntitySearchService;
import com.nimbits.cloudplatform.server.transactions.user.UserHelper;
import com.nimbits.cloudplatform.server.transactions.user.UserServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;

import javax.jdo.PersistenceManagerFactory;
import java.util.*;


public class EntityServiceImpl implements EntityService {
    private final EntityCache cache;

    public EntityServiceImpl(PersistenceManagerFactory pmf, NimbitsCache cache) {
       this.cache = EntityServiceFactory.getCacheInstance(pmf, cache); 
    }

    @Override
    public  Map<String, Point> getActivePoints() {
        return cache.getActivePoints();

    }


    @Override
    public List<Entity> deleteEntity(final User user, final List<Entity> entities)  {
        final List<Entity> deleted;

        if (! entities.isEmpty()) {
            Entity entity = entities.get(0);

            try {
                deleted = cache.deleteEntity(user, entity, Class.forName(entity.getEntityType().getClassName()));



                cache.removeEntityFromCache(deleted);



                if (entity.getEntityType().equals(EntityType.point)) {
                    ValueServiceFactory.getInstance().purgeValues(entity);

                }
                return deleted;
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
        else {
            return Collections.emptyList();
        }


    }



    @Override
    public List<Entity> getEntities(final User user) {
        final List<Entity> retVal =cache.getEntities(user);

        Collections.sort(retVal);
        return retVal;
    }


 
    @Override
    public Entity addUpdateSingleEntity(Entity entity) {
        return addUpdateEntity(Arrays.<Entity>asList(entity)).get(0) ;

    }
    @Override
    public  List<Entity> addUpdateEntity(final List<Entity> list) {

        if (! list.isEmpty()) {
            Entity entity = list.get(0);
            final User u = entity.getEntityType().equals(EntityType.user) ? (User) entity : UserHelper.getUser();
            if (Utils.isEmptyString(entity.getOwner())) {
                entity.setOwner(u.getKey());
            }
            if (Utils.isEmptyString(entity.getParent())) {
                entity.setParent(u.getKey());
            }
            if (Utils.isEmptyString(entity.getUUID())) {
                entity.setUUID(UUID.randomUUID().toString());
            }
            return addUpdateEntity(u, list);
        }
        else {
            return Collections.emptyList();
        }





    }
   


    @Override
    public  List<Entity> findEntityByKey(final User user, final String key)  {


        for (final EntityType t : EntityType.values()) {
            final List<Entity> r;

            r = getEntityByKey(user, key, t);

            if (! r.isEmpty()) {
                return r;

            }
        }
        return new ArrayList<Entity>(0);


    }



    @Override
    public  Map<String, Entity> getEntityModelMap(final User user, final EntityType type, final int limit)  {
        return cache.getEntityMap(user, type, limit);
    }
 
    @Override
    public  List<Entity> getChildren(final User user, final List<Entity> parentEntity)   {
        return cache.getChildren(user, parentEntity);
    }

 
 

    @Override
    public  List<Entity> getEntityByName(final User user, final EntityName name, final EntityType type)  {


        try {
            return cache.getEntityByName(user, name, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            return Collections.emptyList();
        }
        catch (NullPointerException e) {
            return Collections.emptyList();
        }


    }


    @Override
    public  List<Entity> getEntityByTrigger(User user, Entity trigger, EntityType type)  {

        final Class cls;
        try {
            cls = Class.forName(type.getClassName());
            return cache.getEntityByTrigger(user, trigger, cls);
        } catch (ClassNotFoundException e) {
            return Collections.emptyList();
        }



    }


    @Override
    public  Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws Exception {
        return cache.getSystemWideEntityMap(user, type);
    }


    @Override
    public  List<Entity> getIdleEntities() throws Exception {
        return cache.getIdleEntities(UserServiceFactory.getInstance().getAdmin());
    }


    @Override
    public  List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity)  {
        return cache.getSubscriptionsToEntity(user, subscribedEntity);

    }
    @Override
    public  List<Entity> addUpdateEntity(final User user, final Entity entity)  {


        return addUpdateEntity(user, Arrays.asList(entity));

    }

    @Override
    public  List<Entity> addUpdateEntity(final User user, final List<Entity> entity)  {



        List<Entity> result =  cache.addUpdateEntity(user, entity, true);

        EntitySearchService.updateIndex(result);


        return result;

    }


    @Override
    public  List<Entity> getEntityByKey(final User user, final String entityId, final EntityType type)   {
        try {
            if (user != null) {
                return cache.getEntityByKey(user, entityId, Class.forName(type.getClassName()));
            }
            else {

                return cache.getEntityByKey( UserHelper.getUser(), entityId, Class.forName(type.getClassName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }


    }

    @Override
    public  List<Entity> getEntityByUUID(final User user, final String uuid, final EntityType type)   {
        try {
            if (user != null) {

                return cache.getEntityByUUID(user, uuid, Class.forName(type.getClassName()));
            }
            else {

                return cache.getEntityByUUID( UserHelper.getUser(), uuid, Class.forName(type.getClassName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }


    }
}
