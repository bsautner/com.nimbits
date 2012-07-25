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

package com.nimbits.server.transactions.service.entity;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.file.File;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.server.admin.common.ServerInfoImpl;
import com.nimbits.server.api.helper.LocationReportingHelperFactory;
import com.nimbits.server.io.blob.BlobServiceFactory;
import com.nimbits.server.process.task.TaskFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
@SuppressWarnings("unchecked")
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityService {

    private static final long serialVersionUID = -6442025194172745189L;

    private User getUser()  {

        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return UserServiceFactory.getServerInstance().getAnonUser();
        }

    }



    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity) throws NimbitsException {
        final List<Entity> deleted;
        try {
            deleted = EntityTransactionFactory.getInstance(user).deleteEntity(entity, Class.forName(entity.getEntityType().getClassName()));
        } catch (ClassNotFoundException e) {
            throw  new NimbitsException(e);
        }
        for (final Entity e : deleted) {
            EntityTransactionFactory.getInstance(user).removeEntityFromCache(e);
            TaskFactory.getInstance()
                    .startCoreTask(getThreadLocalRequest(), entity, Action.delete, ServerInfoImpl.getFullServerURL(getThreadLocalRequest()));

        }

        if  (entity.getEntityType().equals(EntityType.file)) {
            BlobServiceFactory.getInstance().deleteBlob((File) entity);
        }
        else if (entity.getEntityType().equals(EntityType.point)) {
            ValueServiceFactory.getInstance().purgeValues(entity);

        }
        return deleted;
    }

    @Override
    public List<Entity> getEntityChildren(final User user, final Entity entity,final  EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getChildren(entity, type);
    }


    @Override
    public List<Entity> getEntities() throws NimbitsException {
        final List<Entity> retVal =EntityTransactionFactory.getInstance(getUser()).getEntities();
        Collections.sort(retVal);
        return retVal;
    }


    @Override
    public Entity addUpdateEntity(final Entity entity) throws NimbitsException {
        final User u = entity.getEntityType().equals(EntityType.user) ? (User) entity : getUser();
        if (Utils.isEmptyString(entity.getOwner())) {
            entity.setOwner(u.getKey());
        }
        if (Utils.isEmptyString(entity.getParent())) {
            entity.setParent(u.getKey());
        }
        if (Utils.isEmptyString(entity.getUUID())) {
            entity.setUUID(UUID.randomUUID().toString());
        }
        return addUpdateEntity(u, entity);

    }

    @Override
    public List<Entity> deleteEntity(final Entity entity) throws NimbitsException {
        User u = getUser();
        if (u == null)  {
            u = UserServiceFactory.getInstance().getUserByKey(entity.getOwner(), AuthLevel.admin);
        }
        return  deleteEntity(u, entity);
    }



    @Override
    public List<Entity> getEntityByKey(final String key, final EntityType type) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(getUser()).getEntityByKey(key, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public List<Entity> findEntityByKey(final String key) throws NimbitsException {


        for (final EntityType t : EntityType.values()) {
            final List<Entity> r = getEntityByKey(key, t);
            if (! r.isEmpty()) {
                return r;

            }
        }
        return new ArrayList<Entity>(0);


    }
    @Override
    public List<Entity> findEntityByKey(final User user, final String key) throws NimbitsException {


        for (final EntityType t : EntityType.values()) {
            final List<Entity> r = getEntityByKey(user, key, t);
            if (! r.isEmpty()) {
                return r;

            }
        }
        return new ArrayList<Entity>(0);


    }
    @Override
    public Map<String, Entity> getEntityMap(final EntityType type, final int limit) throws NimbitsException {
        return EntityTransactionFactory.getInstance(getUser()).getEntityMap(type, limit);
    }

    @Override
    public Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getEntityMap(type, limit);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(getUser()).getEntityNameMap(type);
    }

    @Override
    public Entity copyEntity(final Entity originalEntity, final EntityName newName) throws NimbitsException {

        switch (originalEntity.getEntityType()) {

            case user:
                return null;
            case point:
                Point p = PointModelFactory.createPointModel((Point)originalEntity);
                p.setName(newName);
                p.setKey(null);
                return addUpdateEntity(p);
                //return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);

            case category:
                return null;
            case file:
                return null;
            case subscription:
                return null;
            default:
                return null;
        }
    }

    @Override
    public List<Entity> getChildren(final Entity parentEntity, final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(getUser()).getChildren(parentEntity, type);
    }

    @Override
    public List<Entity> getChildren(final User user, final Entity parentEntity, final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getChildren(parentEntity, type);
    }

    @Override
    public List<Entity>  getEntityByName(final User user, final EntityName name, final EntityType type) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(user).getEntityByName(name, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public List<Entity> getEntityByTrigger(User user, Entity trigger, EntityType type) throws NimbitsException {
        try {
            final Class cls = Class.forName(type.getClassName());
            return EntityTransactionFactory.getInstance(user).getEntityByTrigger(trigger, cls);
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }

    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(UserServiceFactory.getServerInstance().getAdmin()).getSystemWideEntityMap(type);
    }

    @Override
    public List<Entity> getIdleEntities() throws NimbitsException {
        return EntityTransactionFactory.getInstance(UserServiceFactory.getServerInstance().getAdmin()).getIdleEntities();
    }

    @Override
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getSubscriptionsToEntity(subscribedEntity);

    }

    @Override
    public Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException {

       TaskFactory.getInstance().startCoreTask(this.getThreadLocalRequest(), entity, Action.update, ServerInfoImpl.getFullServerURL(getThreadLocalRequest()));
       LocationReportingHelperFactory.getInstance().reportLocation(this.getThreadLocalRequest(), entity);
       return EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
    }

    @Override
    public List<Entity>  getEntityByKey(final User user, final String entityId, final EntityType type) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(user).getEntityByKey(entityId, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

}
