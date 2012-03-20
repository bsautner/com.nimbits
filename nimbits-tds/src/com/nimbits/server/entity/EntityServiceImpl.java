package com.nimbits.server.entity;

import com.google.appengine.api.files.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.server.blob.*;
import com.nimbits.server.calculation.*;
import com.nimbits.server.dao.calculation.*;
import com.nimbits.server.intelligence.*;
import com.nimbits.server.orm.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.subscription.*;
import com.nimbits.server.summary.*;
import com.nimbits.server.user.*;
import com.nimbits.server.xmpp.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
@SuppressWarnings("unchecked")
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityTransactions, EntityService {


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
    public Entity getEntityByName(final User user, final EntityName name) {
       return EntityTransactionFactory.getInstance(user).getEntityByName(name);
    }

    private void deleteChildren(final User user, final Entity entity) {
        //TODO - kick of a task to recursivly delete children - all types, and their children etc.
    }


    @Override
    public void deleteEntity(final User user, final Entity entity) {
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
        deleteChildren(user, entity);

    }

    @Override
    public List<Entity> getEntityChildren(final User user, final Entity c,final  EntityType type) {
        return EntityTransactionFactory.getInstance(user).getEntityChildren(c, type);
    }

    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                     this.getThreadLocalRequest());
        } catch (NimbitsException e) {
           return null;
        }
    }


    @Override
    public List<Entity> getEntities() {

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
    public void deleteEntity(Entity entity) {
        User u = getUser();
        if (u == null)  {
            u = UserServiceFactory.getInstance().getUserByUUID(entity.getOwner());
        }
        deleteEntity(u, entity);
    }

    @Override
    public Entity getEntityByUUID(String uuid) {
       return EntityTransactionFactory.getInstance(getUser()).getEntityByUUID(uuid);
    }

    @Override
    public Map<String, Entity> getEntityMap(EntityType type) {
       return EntityTransactionFactory.getInstance(getUser()).getEntityMap(type);
    }

    @Override
    public Map<String, Entity> getEntityMap(User user, EntityType type) {
        return EntityTransactionFactory.getInstance(user).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) {
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
        return EntityTransactionFactory.getInstance(getUser()).getEntityChildren(parentEntity, type);
    }


    @Override
    public List<Entity> getEntityChildren(Entity parentEntity, EntityType type) {
        return EntityTransactionFactory.getInstance(getUser()).getEntityChildren(parentEntity, type);
    }

    @Override
    public Entity getEntityByName(EntityName name) {
       return EntityTransactionFactory.getInstance(getUser()).getEntityByName(name);
    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(EntityType type) {
        return EntityTransactionFactory.getInstance(null).getSystemWideEntityMap(type);
    }

    @Override
    public Entity addUpdateEntity(User user, Entity entity) throws NimbitsException {
        return EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
    }

    @Override
    public Entity getEntityByUUID(User user, String entityId) {
        return EntityTransactionFactory.getInstance(user).getEntityByUUID(entityId);
    }


}
