/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.entity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:02 PM
 */
@RemoteServiceRelativePath("entityService")
public interface EntityService extends RemoteService {

    List<Entity> getEntities(final User u) throws NimbitsException;

    Entity addUpdateEntity(final Entity entity) throws NimbitsException;

    Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException;

    List<Entity> deleteEntity(final Entity entity) throws NimbitsException;

    Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException;

    Entity copyEntity(final Entity originalEntity, final EntityName newName) throws NimbitsException;

   // List<Entity> getChildren(final Entity parentEntity, final EntityType type) throws NimbitsException;

    List<Entity> findEntityByKey(final User u, final String key) throws NimbitsException;

    List<Entity> getEntityByKey(final User u, final String key, final EntityType type) throws NimbitsException;

    List<Entity> getEntityByName(final User u, final EntityName name, final EntityType type) throws NimbitsException;

    List<Entity> deleteEntity(final User u, final Entity entity) throws NimbitsException;

   // List<Entity> getEntityChildren(final User u, final Entity c, final EntityType point) throws NimbitsException;

    List<Entity> getEntityByTrigger(final User user, final Entity entity, final EntityType type) throws NimbitsException;

    List<Entity> getIdleEntities() throws NimbitsException;

    List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(final User u,final EntityType type) throws NimbitsException;


    List<Entity> getChildren(final User user, final List<Entity> parentEntity) throws NimbitsException;

    void updateUser(final User user) throws NimbitsException;


    static class App {
        private static EntityServiceAsync ourInstance = GWT.create(EntityService.class);

        public static synchronized EntityServiceAsync getInstance() {
            return ourInstance;
        }
    }

}
