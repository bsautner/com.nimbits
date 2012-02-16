package com.nimbits.client.service.entity;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;

import java.util.List;
import java.util.Map;

public interface EntityServiceAsync {
    void getEntities(AsyncCallback<List<Entity>> async);

    void addUpdateEntity(final Entity entity, AsyncCallback<Entity> async);

    void deleteEntity(Entity entity, AsyncCallback<Void> asyncCallback);

    void getEntityByUUID(String uuid, AsyncCallback<Entity> async);

    void copyEntity(Entity originalEntity, EntityName newName, AsyncCallback<Entity> async);

    void getEntityNameMap(EntityType type, AsyncCallback<Map<EntityName, Entity>> async);

    void getEntityMap(EntityType type, AsyncCallback<Map<String, Entity>> async);

    void getChildren(Entity parentEntity, EntityType type, AsyncCallback<List<Entity>> async);

    void getEntityChildren(Entity parentEntity, EntityType type, AsyncCallback<List<Entity>> async);

    void getEntityByName(EntityName name, AsyncCallback<Entity> async);

    void addUpdateEntity(User user, Entity aConnection, AsyncCallback<Entity> async);

    void getEntityByUUID(User u, String entityId, AsyncCallback<Entity> async);

    void getEntityByName(User u, EntityName name, AsyncCallback<Entity> async);

    void deleteEntity(User u, Entity entity, AsyncCallback<Void> async);

    void getEntityChildren(User u, Entity c, EntityType point, AsyncCallback<List<Entity>> async);
}
