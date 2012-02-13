package com.nimbits.client.service.entity;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;

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
}
