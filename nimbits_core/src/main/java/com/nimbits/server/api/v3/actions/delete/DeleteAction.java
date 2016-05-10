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

package com.nimbits.server.api.v3.actions.delete;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.v3.actions.RestAction;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class DeleteAction extends RestAction {



    public DeleteAction(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao) {
        super(entityService, valueService, userService, entityDao);

    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        String path = req.getRequestURI();
        String uuid = getEntityUUID(path);
        Optional<Entity> optional = entityDao.findEntity(user, uuid);
        if (optional.isPresent()) {
            Entity entity = optional.get();
            if (!user.getIsAdmin() && entity.getEntityType() != EntityType.user && entity.getOwner().equals(user.getId())) {
                entityService.deleteEntity(user, entity);
            } else if (user.getIsAdmin()) {
                entityService.deleteEntity(user, entity);
            } else if (!entity.getOwner().equals(user.getId())) {
                throw new SecurityException("You can not delete an entity you don't own if your not the system admin");
            }
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
