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

package com.nimbits.server.transaction.entity.service;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EntityService {


    void deleteEntity(final User user, final Entity entities);

    List<Entity> getEntities(final User user);

    List<Entity> getEntitiesByType(final User user, final EntityType type);

    Entity addUpdateIncompleteEntity(final ValueService valueService, final User user, final Entity entity);

    Entity addUpdateEntity(final ValueService valueService, final User user, final Entity entity);

}
