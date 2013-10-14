/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.service.entity;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.user.User;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface EntityServiceRpcAsync {


    void addUpdateEntityRpc(final List<Entity> entity, AsyncCallback< List<Entity>> async);

    void copyEntity(Entity originalEntity, EntityName newName, AsyncCallback< List<Entity>> async);

   // void getEntityNameMap(EntityType type, AsyncCallback<Map<EntityName, Entity>> async);

    void getEntityByKeyRpc(User u, String entityId, EntityType type, AsyncCallback<List<Entity>> async);

    void deleteEntityRpc(List<Entity> entity, AsyncCallback<List<Entity>> asyncCallback);

    void getEntityMapRpc(User user, int type, final int limit, AsyncCallback<Map<String, Entity>> async);


    //void updateUser(User user, AsyncCallback<Void> async);

    void getEntitiesRpc(User user, AsyncCallback<List<Entity>> async)  ;
}
