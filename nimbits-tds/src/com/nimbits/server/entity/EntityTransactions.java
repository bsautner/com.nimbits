package com.nimbits.server.entity;


import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

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



}
