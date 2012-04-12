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

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 11:46 AM
 */
public interface EntityTransactions {

    Map<String, Entity> getEntityMap( final EntityType type, final int limit) throws NimbitsException;

    Map<EntityName, Entity> getEntityNameMap( final EntityType type) throws NimbitsException;

    List<Entity> getChildren( final Entity parentEntity,  final EntityType type) throws NimbitsException;

    Entity addUpdateEntity( final Entity entity) throws NimbitsException;

    List<Entity> getEntities() throws NimbitsException;

    List<Entity> deleteEntity( final Entity entity,final Class<?> cls) throws NimbitsException;

    List<Entity> getEntityByKey( final String uuid,  final Class<?> cls) throws NimbitsException;

    Map<String, Entity> getSystemWideEntityMap(final EntityType type) throws NimbitsException;

    void removeEntityFromCache( final Entity entity) throws NimbitsException;

    List<Entity> getEntityByName( final EntityName name,  final Class<?> cls) throws NimbitsException;
}
