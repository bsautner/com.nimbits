package com.nimbits.client.service.entity;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

public interface EntityServiceAsync {
    void getEntities(AsyncCallback<List<Entity>> async);

    void addUpdateEntity(final Entity entity, AsyncCallback<Entity> async);

    void deleteEntity(Entity entity, AsyncCallback<Void> asyncCallback);

    void getEntityByUUID(String uuid, AsyncCallback<Entity> async);

    void getEntityMap(EntityType type, boolean includeValues, AsyncCallback<Map<String, Entity>> async);

}
