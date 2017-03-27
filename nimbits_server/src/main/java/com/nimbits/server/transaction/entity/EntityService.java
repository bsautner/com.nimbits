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

package com.nimbits.server.transaction.entity;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EntityService {

    private static final String POINT_INITIALISED = "POINT_INITIALISED";


    private final EntityDao entityDao;
    private final ValueService valueService;
    @org.springframework.beans.factory.annotation.Value("${system.init_points}")
    private boolean initPoints;


    @Autowired
    public EntityService(EntityDao entityDao, ValueService valueService) {

        this.entityDao = entityDao;
        this.valueService = valueService;

    }

    public void deleteEntity(final User user, final Entity entity) {

        List<Entity> children = entityDao.getChildren(user, entity);


        for (Entity c : children) {
            deleteEntity(user, c);
        }
        entityDao.deleteEntity(user, entity, entity.getEntityType());


    }

    public List<Entity> getEntities(final User user) {
        final List<Entity> retVal = entityDao.getEntities(user);

        Collections.sort(retVal);
        return retVal;
    }


    public Entity addEntity(final User user, Entity entity) {


        Entity created = entityDao.addEntity(user, entity);

        if (initPoints && EntityType.point.equals(entity.getEntityType())) {

            Value init = new Value.Builder().doubleValue(0.0).timestamp(0L).meta(POINT_INITIALISED).create();

            valueService.recordValues(user, (Point) created, Collections.singletonList(init));


        }

        return created;

    }

}


