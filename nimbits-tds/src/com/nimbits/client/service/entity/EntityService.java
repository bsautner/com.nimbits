package com.nimbits.client.service.entity;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:02 PM
 */
@RemoteServiceRelativePath(Const.PARAM_ENTITY)
public interface EntityService extends RemoteService {
    List<Entity> getEntities() throws NimbitsException;

    Entity addUpdateEntity(final Entity entity) throws NimbitsException;

    void deleteEntity(Entity entity);

    Entity getEntityByUUID(String uuid);

    Map<String, Entity> getEntityMap(EntityType type);

    Map<EntityName, Entity> getEntityNameMap(EntityType type);

    Entity copyEntity(Entity originalEntity, EntityName newName);




}
