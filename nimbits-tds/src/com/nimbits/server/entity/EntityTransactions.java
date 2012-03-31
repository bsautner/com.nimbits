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

package com.nimbits.server.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;

import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 11:46 AM
 */
public interface EntityTransactions {

    Map<String, Entity> getEntityMap(EntityType type) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap(EntityType type) throws NimbitsException;

    List<Entity> getChildren(Entity parentEntity, EntityType type);

    Entity addUpdateEntity(Entity entity) throws NimbitsException;

    List<Entity> getEntities() throws NimbitsException;

    List<Entity> deleteEntity(Entity entity) throws NimbitsException;

    Entity getEntityByUUID(String uuid) throws NimbitsException;

    Entity getEntityByName(EntityName name) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(EntityType type) throws NimbitsException;

    void removeEntityFromCache(Entity entity) throws NimbitsException;
}
