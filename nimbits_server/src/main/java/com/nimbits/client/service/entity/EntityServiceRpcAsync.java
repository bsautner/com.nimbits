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

import com.nimbits.client.model.user.User;

public interface EntityServiceRpcAsync {

    void addUpdateEntityRpc(final User user, com.nimbits.client.model.entity.Entity entity, com.google.gwt.user.client.rpc.AsyncCallback<com.nimbits.client.model.entity.Entity> arg2);

    void copyEntity(final User user, com.nimbits.client.model.entity.Entity originalEntity, com.nimbits.client.model.entity.EntityName newName, com.google.gwt.user.client.rpc.AsyncCallback<com.nimbits.client.model.entity.Entity> arg3);

    void deleteEntityRpc(final User user, com.nimbits.client.model.entity.Entity entity, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> arg2);

    void getEntitiesRpc(com.nimbits.client.model.user.User user, com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<com.nimbits.client.model.entity.Entity>> arg2);

    void getEntityByKeyRpc(com.nimbits.client.model.user.User u, java.lang.String key, com.nimbits.client.enums.EntityType type, com.google.gwt.user.client.rpc.AsyncCallback<com.nimbits.client.model.entity.Entity> arg4);

    void getEntityMapRpc(final User user, int type, int limit, com.google.gwt.user.client.rpc.AsyncCallback<java.util.Map<java.lang.String, com.nimbits.client.model.entity.Entity>> arg3);

}
