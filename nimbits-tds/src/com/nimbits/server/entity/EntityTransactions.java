package com.nimbits.server.entity;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 11:46 AM
 */
public interface EntityTransactions {

    Map<String, Entity> getEntityMap(EntityType type) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException;

    List<Entity> getChildren(Entity parentEntity, EntityType type);

    Entity addUpdateEntity(Entity entity) throws NimbitsException;

    List<Entity> getEntities() throws NimbitsException;

    void deleteEntity(Entity entity) throws NimbitsException;

    Entity getEntityByUUID(String uuid) throws NimbitsException;

    Entity getEntityByName(EntityName name) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(EntityType type) throws NimbitsException;

    void removeEntityFromCache(Entity entity) throws NimbitsException;
}
