package com.nimbits.server.entity;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.*;
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
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.intelligence.IntelligenceServiceFactory;
import com.nimbits.server.orm.entity.EntityStore;
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
    public Entity addUpdateEntity(EntityName name, EntityType type) throws NimbitsException {
        User u = getUser();

        Entity e = EntityModelFactory.createEntity(name, "", type, ProtectionLevel.everyone,
                UUID.randomUUID().toString(), u.getUuid(), u.getUuid());
        Entity r = EntityTransactionFactory.getInstance(u).addUpdateEntity(e);
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
    public void deleteEntity(final User user, final Entity entity) throws NimbitsException {
        EntityTransactionFactory.getInstance(user).deleteEntity(entity);

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

            FeedServiceFactory.getInstance().postToFeed(user,entity.getEntityType().name() +
                    " " + entity.getName().toString() + " deleted ", FeedType.info);



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
    public Entity addUpdateEntity(Entity entity) throws NimbitsException {
        User u = getUser();
        if (Utils.isEmptyString(entity.getOwner())) {
            entity.setOwner(u.getUuid());
        }
        if (Utils.isEmptyString(entity.getParent())) {
            entity.setParent(u.getUuid());
        }
        if (Utils.isEmptyString(entity.getEntity())) {
            entity.setEntity(UUID.randomUUID().toString());
        }
        return addUpdateEntity(u, entity);
    }

    @Override
    public void deleteEntity(Entity entity) throws NimbitsException {
        User u = getUser();
        if (u == null)  {
            u = UserServiceFactory.getInstance().getUserByUUID(entity.getOwner());
        }
        deleteEntity(u, entity);
    }

    @Override
    public Entity getEntityByUUID(String uuid) throws NimbitsException {
       return EntityTransactionFactory.getInstance(getUser()).getEntityByUUID(uuid);
    }

    @Override
    public Map<String, Entity> getEntityMap(EntityType type) throws NimbitsException {
       return EntityTransactionFactory.getInstance(getUser()).getEntityMap(type);
    }

    @Override
    public Map<String, Entity> getEntityMap(User user, EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(getUser()).getEntityNameMap(type);
    }

    @Override
    public Entity copyEntity(Entity originalEntity, EntityName newName) throws NimbitsException {
        Entity newEntity = new EntityStore(originalEntity);
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
    public List<Entity> getChildren(Entity parentEntity, EntityType type) {
        return EntityTransactionFactory.getInstance(getUser()).getChildren(parentEntity, type);
    }



    @Override
    public Entity getEntityByName(EntityName name) throws NimbitsException {
       return EntityTransactionFactory.getInstance(getUser()).getEntityByName(name);
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(EntityType type) throws NimbitsException {
        return EntityTransactionFactory.getInstance(null).getSystemWideEntityMap(type);
    }

    @Override
    public void removeEntityFromCache(Entity entity) throws NimbitsException {
        throw new NimbitsException(UserMessages.ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public Entity addUpdateEntity(User user, Entity entity) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
    }

    @Override
    public Entity getEntityByUUID(User user, String entityId) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).getEntityByUUID(entityId);
    }


}
