package com.nimbits.client.service.entity;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:02 PM
 */
@RemoteServiceRelativePath(Const.PARAM_ENTITY)
public interface EntityService extends RemoteService {
    List<Entity> getEntities();

    Entity addUpdateEntity(final Entity entity) throws NimbitsException;

    Entity addUpdateEntity(final EntityName entity, final EntityType type) throws NimbitsException;

    void deleteEntity(Entity entity);

    Entity getEntityByUUID(String uuid);

    Map<String, Entity> getEntityMap(EntityType type);

    Map<String, Entity> getEntityMap(User user, EntityType type);

    Map<EntityName, Entity> getEntityNameMap(EntityType type);

    Entity copyEntity(Entity originalEntity, EntityName newName) throws NimbitsException;

    List<Entity> getChildren(Entity parentEntity, EntityType type);

    List<Entity> getEntityChildren(Entity parentEntity, EntityType type);

    Entity getEntityByName(EntityName name);

    Entity addUpdateEntity(User user, Entity aConnection) throws NimbitsException;

    Entity getEntityByUUID(User u, String entityId);

    Entity getEntityByName(User u, EntityName name);

    void deleteEntity(User u, Entity entity);

    List<Entity> getEntityChildren(User u, Entity c, EntityType point);

    Map<String, Entity> getSystemWideEntityMap(EntityType type);


}
