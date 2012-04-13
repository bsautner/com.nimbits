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

package com.nimbits.server.entity;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.server.blob.*;
import com.nimbits.server.core.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.point.*;
import com.nimbits.server.user.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
@SuppressWarnings("unchecked")
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityTransactions, EntityService {

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
            CoreFactory.getInstance().reportDeleteToCore(entity);
            FeedServiceFactory.getInstance().postToFeed(user,entity.getEntityType().name() +
                    ' ' + entity.getName().toString() + " deleted ", FeedType.info);
        }
        //todo - delete or disable subscriptions to entity


        if  (entity.getEntityType().equals(EntityType.file)) {
            BlobServiceFactory.getInstance().deleteBlob(entity);
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
    public List<Entity> deleteEntity(final Entity entity, final Class<?> cls) throws NimbitsException {
        throw new NimbitsException("not implemented");
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

        final Entity e=  addUpdateEntity(u, entity);
        CoreFactory.getInstance().reportUpdateToCore(e);
        return e;
    }

    @Override
    public List<Entity> deleteEntity(final Entity entity) throws NimbitsException {
        User u = getUser();
        if (u == null)  {
            u = UserServiceFactory.getInstance().getUserByKey(entity.getOwner());
        }
        return  deleteEntity(u, entity);
    }



    @Override
    public List<Entity>  getEntityByKey(final String key, final String className) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(getUser()).getEntityByKey(key, Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
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
        final Entity newEntity = new EntityStore(originalEntity);

        switch (newEntity.getEntityType()) {

            case user:
                return null;
            case point:
                return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);
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
    public List<Entity>  getEntityByName(final User user, final EntityName name, final EntityType type) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(user).getEntityByName(name, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public List<Entity>  getEntityByName(final User user, final EntityName name, final String className) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(user).getEntityByName(name, Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public List<Entity>  getEntityByName(final EntityName name, final EntityType type) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(getUser()).getEntityByName(name,Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(UserServiceFactory.getServerInstance().getAdmin()).getSystemWideEntityMap(type);
    }

    @Override
    public void removeEntityFromCache(final Entity entity) throws NimbitsException {
        throw new NimbitsException(UserMessages.ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public List<Entity>  getEntityByName(EntityName name, Class<?> cls) throws NimbitsException {
        return EntityTransactionFactory.getInstance(getUser()).getEntityByName(name, cls);
    }

    @Override
    public Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
    }

    @Override
    public List<Entity>  getEntityByKey(final User user, final String entityId, final String className) throws NimbitsException {
        try {
            return EntityTransactionFactory.getInstance(user).getEntityByKey(entityId, Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public List<Entity>  getEntityByKey(String uuid, Class<?> cls) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }
}
