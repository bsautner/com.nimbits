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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FeedType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.server.blob.BlobServiceFactory;
import com.nimbits.server.calculation.CalculationServiceFactory;
import com.nimbits.server.core.CoreFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.intelligence.IntelligenceServiceFactory;
import com.nimbits.server.orm.EntityStore;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.subscription.SubscriptionServiceFactory;
import com.nimbits.server.summary.SummaryServiceFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.xmpp.XmppServiceFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
@SuppressWarnings("unchecked")
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityTransactions, EntityService {

    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }

    @Override
    public Entity addUpdateEntity(final EntityName name, final EntityType type) throws NimbitsException {
        final User u = getUser();

        final Entity e = EntityModelFactory.createEntity(name, "", type, ProtectionLevel.everyone,
                UUID.randomUUID().toString(), u.getUuid(), u.getUuid());
        final Entity r = EntityTransactionFactory.getInstance(u).addUpdateEntity(e);
        switch (type) {
            case point:
                PointServiceFactory.getInstance().addPoint(u, r);


        }

        return r;
    }

    @Override
    public Entity getEntityByName(final User user, final EntityName name) throws NimbitsException {
       return EntityTransactionFactory.getInstance(user).getEntityByName(name);
    }

    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity) throws NimbitsException {
       final List<Entity> deleted =  EntityTransactionFactory.getInstance(user).deleteEntity(entity);
        for (final Entity e : deleted) {
            EntityTransactionFactory.getInstance(user).removeEntityFromCache(e);
            CoreFactory.getInstance().reportDeleteToCore(entity);
            FeedServiceFactory.getInstance().postToFeed(user,entity.getEntityType().name() +
                    ' ' + entity.getName().toString() + " deleted ", FeedType.info);
        }
        //todo - delete or disable subscriptions to entity


        switch (entity.getEntityType()) {

            case user:
                break;
            case point:
                PointServiceFactory.getInstance().deletePoint(user, entity);

                break;
            case category:

                break;
            case file:
                BlobServiceFactory.getInstance().deleteBlob(entity);
                break;
            case subscription:
                SubscriptionServiceFactory.getInstance().deleteSubscription(user, entity);
                break;
            case userConnection:
                break;
            case calculation:
                CalculationServiceFactory.getInstance().deleteCalculation(user, entity);
                break;
            case intelligence:
                IntelligenceServiceFactory.getInstance().deleteIntelligence(user, entity);
                break;
            case feed:
                break;
            case resource:
                XmppServiceFactory.getInstance().deleteResource(user, entity);
                break;
            case summary:
                SummaryServiceFactory.getInstance().deleteSummary(user, entity);
                break;
        }

         return deleted;



    }

    @Override
    public List<Entity> getEntityChildren(final User user, final Entity entity,final  EntityType type) {
        return EntityTransactionFactory.getInstance(user).getChildren(entity, type);
    }


    @Override
    public List<Entity> getEntities() throws NimbitsException {

         return EntityTransactionFactory.getInstance(getUser()).getEntities();
    }

    @Override
    public Entity addUpdateEntity(final Entity entity) throws NimbitsException {
        final User u = getUser();
        if (Utils.isEmptyString(entity.getOwner())) {
            entity.setOwner(u.getUuid());
        }
        if (Utils.isEmptyString(entity.getParent())) {
            entity.setParent(u.getUuid());
        }
        if (Utils.isEmptyString(entity.getEntity())) {
            entity.setEntity(UUID.randomUUID().toString());
        }

        final Entity e=  addUpdateEntity(u, entity);
        CoreFactory.getInstance().reportUpdateToCore(e);
        return e;
    }

    @Override
    public List<Entity> deleteEntity(final Entity entity) throws NimbitsException {
        User u = getUser();
        if (u == null)  {
            u = UserServiceFactory.getInstance().getUserByUUID(entity.getOwner());
        }
       return  deleteEntity(u, entity);
    }

    @Override
    public Entity getEntityByUUID(final String uuid) throws NimbitsException {
       return EntityTransactionFactory.getInstance(getUser()).getEntityByUUID(uuid);
    }

    @Override
    public Map<String, Entity> getEntityMap(final EntityType type) throws NimbitsException {
       return EntityTransactionFactory.getInstance(getUser()).getEntityMap(type);
    }

    @Override
    public Map<String, Entity> getEntityMap(final User user, final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(getUser()).getEntityNameMap(type);
    }

    @Override
    public Entity copyEntity(final Entity originalEntity, final EntityName newName) throws NimbitsException {
        final Entity newEntity = new EntityStore(originalEntity);
        newEntity.setEntity(UUID.randomUUID().toString());
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
    public List<Entity> getChildren(final Entity parentEntity, final EntityType type) {
        return EntityTransactionFactory.getInstance(getUser()).getChildren(parentEntity, type);
    }



    @Override
    public Entity getEntityByName(final EntityName name) throws NimbitsException {
       return EntityTransactionFactory.getInstance(getUser()).getEntityByName(name);
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(null).getSystemWideEntityMap(type);
    }

    @Override
    public void removeEntityFromCache(final Entity entity) throws NimbitsException {
        throw new NimbitsException(UserMessages.ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
    }

    @Override
    public Entity getEntityByUUID(final User user, final String entityId) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getEntityByUUID(entityId);
    }


}
