package com.nimbits.server.entity;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.server.orm.entity.EntityStore;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.user.UserServiceFactory;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityService {


    private HttpSession getSession() {
             // Get the current request and then return its session
             return this.getThreadLocalRequest().getSession();
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
    public Entity addUpdateEntity(Entity entity) {
        if (entity.getOwner() == null) {
            entity.setOwner(getUser().getUuid());
        }
        if (entity.getParent() == null) {
            entity.setParent(getUser().getUuid());
        }
        if (entity.getEntity() == null) {
            entity.setEntity(UUID.randomUUID().toString());
        }
        return EntityTransactionFactory.getInstance(getUser()).addUpdateEntity(entity);
    }

    @Override
    public void deleteEntity(Entity entity) {
        EntityTransactionFactory.getInstance(getUser()).deleteEntity(entity);
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
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) {
        return EntityTransactionFactory.getInstance(getUser()).getEntityNameMap(type);
    }



    @Override
    public Entity copyEntity(Entity originalEntity, EntityName newName) {
        Entity newEntity = new EntityStore(originalEntity);
        newEntity.setEntity(UUID.randomUUID().toString());
        switch (newEntity.getEntityType()) {


            case user:
                 return null;
            case point:
                return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);
            case category:
                return null;
            case diagram:
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


}
