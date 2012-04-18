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
import com.nimbits.client.model.point.Point;
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

    List<Entity> deleteEntity(Entity entity) throws NimbitsException;

    Map<String, Entity> getEntityMap(EntityType type, final int limit) throws NimbitsException;

    Map<String, Entity> getEntityMap(User user, EntityType type, final int limit) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException;

    Entity copyEntity(Entity originalEntity, EntityName newName) throws NimbitsException;

    List<Entity> getChildren(Entity parentEntity, EntityType type) throws NimbitsException;

    Entity addUpdateEntity(User user, Entity entity) throws NimbitsException;

    List<Entity> getEntityByKey(String uuid, String className) throws NimbitsException;

    List<Entity> getEntityByKey(User u, String entityId, String className) throws NimbitsException;

    List<Entity> getEntityByName(User u, EntityName name, EntityType type) throws NimbitsException;

    List<Entity> getEntityByName(EntityName name, EntityType type) throws NimbitsException;

    List<Entity> deleteEntity(User u, Entity entity) throws NimbitsException;

    List<Entity> getEntityChildren(User u, Entity c, EntityType point) throws NimbitsException;

    List<Entity>  getEntityByName(User user, EntityName name,String className) throws NimbitsException;

    List<Entity> getEntitiesBySource(Entity source, EntityType type) throws NimbitsException;

    List<Point> getIdlePoints() throws NimbitsException;
}
