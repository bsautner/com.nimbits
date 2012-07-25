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

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:02 PM
 */
@RemoteServiceRelativePath("entity")
public interface EntityService extends RemoteService {

    List<Entity> getEntities() throws NimbitsException;

    Entity addUpdateEntity(final Entity entity) throws NimbitsException;

    Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException;

    List<Entity> deleteEntity(final Entity entity) throws NimbitsException;

    Map<String, Entity> getEntityMap(final EntityType type, final int limit) throws NimbitsException;

    Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException;

    Entity copyEntity(final Entity originalEntity, final EntityName newName) throws NimbitsException;

    List<Entity> getChildren(final Entity parentEntity, final EntityType type) throws NimbitsException;



    List<Entity> findEntityByKey(final String key) throws NimbitsException;

    List<Entity> getEntityByKey(final String key, final EntityType type) throws NimbitsException;

    List<Entity> getEntityByKey(final User u, final String key, final EntityType type) throws NimbitsException;

    List<Entity> getEntityByName(final User u, final EntityName name, final EntityType type) throws NimbitsException;

    List<Entity> deleteEntity(final User u, final Entity entity) throws NimbitsException;

    List<Entity> getEntityChildren(final User u, final Entity c, final EntityType point) throws NimbitsException;

    List<Entity> getEntityByTrigger(final User user, final Entity entity, final EntityType type) throws NimbitsException;

    List<Entity> getIdleEntities() throws NimbitsException;

    List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException;


    List<Entity> findEntityByKey(User user, String param) throws NimbitsException;

    List<Entity> getChildren(User user, Entity parentEntity, EntityType type) throws NimbitsException;
}
