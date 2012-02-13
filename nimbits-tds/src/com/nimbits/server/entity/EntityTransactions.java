package com.nimbits.server.entity;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;

import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:48 AM
 */
public interface EntityTransactions {

    Entity addUpdateEntity(Entity entity);

    List<Entity> getEntities();

    void deleteEntity(Entity entity);

    Entity getEntityByUUID(String uuid);

    Map<String,Entity> getEntityMap(EntityType type);

    Map<EntityName,Entity> getEntityNameMap(EntityType type);

    List<Entity> getEntityChildren(Entity parentEntity, EntityType type);
}
