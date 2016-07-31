/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.service.entity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;

import java.util.List;
import java.util.Map;


@RemoteServiceRelativePath("rpc/entityService")
public interface EntityServiceRpc extends RemoteService {


    Entity addUpdateEntityRpc(final Entity entity) throws Exception;

    void deleteEntityRpc(final  Entity  entity) throws Exception;

    Map<String, Entity> getEntityMapRpc(final int type, final int limit);

    Entity copyEntity(final Entity originalEntity, final EntityName newName);

     Entity getEntityByKeyRpc(final User u, final String key, final EntityType type);

    List<Entity> getEntitiesRpc(User user) throws Exception;


    class App {
        private static EntityServiceRpcAsync ourInstance = GWT.create(EntityServiceRpc.class);

        public static synchronized EntityServiceRpcAsync getInstance() {
            return ourInstance;
        }
    }

}
