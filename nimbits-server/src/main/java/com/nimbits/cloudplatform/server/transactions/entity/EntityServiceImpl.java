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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.service.entity.EntityService;
import com.nimbits.cloudplatform.server.transactions.search.EntitySearchService;
import com.nimbits.cloudplatform.server.transactions.user.UserHelper;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
@SuppressWarnings("unchecked")

@Service("entityService")
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityService {

    private static final long serialVersionUID = -6442025194172745189L;
    private static Logger log = Logger.getLogger(EntityServiceImpl.class.getName());
    public  static Map<String, Point> getActivePoints() {
        return EntityCache.getActivePoints();

    }


    public static List<Entity> deleteEntity(final User user, final List<Entity> entities)  {
        final List<Entity> deleted;

        if (! entities.isEmpty()) {
            Entity entity = entities.get(0);

            try {
                deleted = EntityCache.deleteEntity(user, entity, Class.forName(entity.getEntityType().getClassName()));



                EntityCache.removeEntityFromCache(deleted);



                if (entity.getEntityType().equals(EntityType.point)) {
                    ValueTransaction.purgeValues(entity);

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



    public static List<Entity> getEntities(final User user) {
        final List<Entity> retVal =EntityCache.getEntities(user);

        Collections.sort(retVal);
        return retVal;
    }


    @Override
    public List<Entity> addUpdateEntityRpc(final List<Entity> entity) throws ClassNotFoundException {
        return addUpdateEntity(entity);

    }
    public static Entity addUpdateSingleEntity(Entity entity) {
        return addUpdateEntity(Arrays.<Entity>asList(entity)).get(0) ;

    }
    public static List<Entity> addUpdateEntity(final List<Entity> list) {

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
    public List<Entity> deleteEntityRpc(final List<Entity> entityList) {
        User u = UserHelper.getUser();
        if (u != null && ! entityList.isEmpty())  {

            return  deleteEntity(u, entityList);
        }
        else {
            return Collections.emptyList();
        }

    }


    public static List<Entity> findEntityByKey(final User user, final String key)  {


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
    public Map<String, Entity> getEntityMapRpc(final User user, final int type, final int limit) {
        return getEntityModelMap(user, EntityType.get(type), limit);
    }

    public static Map<String, Entity> getEntityModelMap(final User user, final EntityType type, final int limit)  {
        return EntityCache.getEntityMap(user, type, limit);
    }
//    @Override
//    public Map<EntityName, Entity> getEntityNameMap(final EntityType type)  {
//        return EntityCache.getEntityNameMap( UserHelper.getUser(), type);
//    }

    @Override
    public List<Entity> copyEntity(final Entity originalEntity, final EntityName newName)  {

        switch (originalEntity.getEntityType()) {

            case user:
                return null;
            case point:
                Point p = PointModelFactory.createPointModel((Point)originalEntity);
                p.setName(newName);
                p.setKey(null);
                List<Entity> list = new ArrayList<Entity>(1);
                list.add(p);
                return addUpdateEntity(list);
            //return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);

            case category:
                return null;

            case subscription:
                return null;
            default:
                return null;
        }
    }

//    @Override
//    public List<Entity> getChildren(final Entity parentEntity, final EntityType type)  {
//        return EntityCache.getChildren(getUser(), parentEntity, type);
//    }


    public static List<Entity> getChildren(final User user, final  List<Entity> parentEntity) throws Exception {
        return EntityCache.getChildren(user, parentEntity);
    }

//    @Override
//    public void updateUser(User user)  {
//        EntityCache.updateUser(user);
//    }

    @Override
    public List<Entity> getEntitiesRpc(final User user)  {

        return getEntities(user);
    }

    public static List<Entity> getEntityByName(final User user, final EntityName name, final EntityType type)  {


        try {
            return EntityCache.getEntityByName(user, name, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            return Collections.emptyList();
        }
        catch (NullPointerException e) {
            return Collections.emptyList();
        }


    }


    public static List<Entity> getEntityByTrigger(User user, Entity trigger, EntityType type)  {

        final Class cls;
        try {
            cls = Class.forName(type.getClassName());
            return EntityCache.getEntityByTrigger(user, trigger, cls);
        } catch (ClassNotFoundException e) {
            return Collections.emptyList();
        }



    }


    public static Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws Exception {
        return EntityCache.getSystemWideEntityMap(user, type);
    }


    public static List<Entity> getIdleEntities() throws Exception {
        return EntityCache.getIdleEntities(UserTransaction.getAdmin());
    }


    public static List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity)  {
        return EntityCache.getSubscriptionsToEntity(user, subscribedEntity);

    }
    public static List<Entity> addUpdateEntity(final User user, final  Entity  entity)  {


        return addUpdateEntity(user, Arrays.asList(entity));

    }

    public static List<Entity> addUpdateEntity(final User user, final List<Entity> entity)  {



        List<Entity> result =  EntityCache.addUpdateEntity(user, entity, true);

        EntitySearchService.updateIndex(result);


        return result;

    }

    @Override
    public List<Entity> getEntityByKeyRpc(final User user, final String entityId, final EntityType type)  {
        return getEntityByKey(user, entityId, type);
    }


    public static List<Entity> getEntityByKey(final User user, final String entityId, final EntityType type)   {
        try {
            if (user != null) {
                return EntityCache.getEntityByKey(user, entityId, Class.forName(type.getClassName()));
            }
            else {

                return EntityCache.getEntityByKey( UserHelper.getUser(), entityId, Class.forName(type.getClassName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }


    }

    public static List<Entity> getEntityByUUID(final User user, final String uuid, final EntityType type)   {
        try {
            if (user != null) {
                return EntityCache.getEntityByUUID(user, uuid, Class.forName(type.getClassName()));
            }
            else {

                return EntityCache.getEntityByUUID( UserHelper.getUser(), uuid, Class.forName(type.getClassName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }


    }
}
