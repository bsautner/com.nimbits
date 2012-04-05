/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

    Entity addUpdateEntity(final EntityName entity, final EntityType type) throws NimbitsException;

    List<Entity> deleteEntity(Entity entity) throws NimbitsException;



    Map<String, Entity> getEntityMap(EntityType type) throws NimbitsException;

    Map<String, Entity> getEntityMap(User user, EntityType type) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException;

    Entity copyEntity(Entity originalEntity, EntityName newName) throws NimbitsException;

    List<Entity> getChildren(Entity parentEntity, EntityType type) throws NimbitsException;



    Entity addUpdateEntity(User user, Entity aConnection) throws NimbitsException;

    Entity getEntityByKey(String uuid) throws NimbitsException;

    Entity getEntityByKey(User u, String entityId) throws NimbitsException;

    Entity getEntityByName(User u, EntityName name, EntityType type) throws NimbitsException;

    Entity getEntityByName(EntityName name, EntityType type) throws NimbitsException;

    List<Entity> deleteEntity(User u, Entity entity) throws NimbitsException;

    List<Entity> getEntityChildren(User u, Entity c, EntityType point) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(EntityType type) throws NimbitsException;


}
