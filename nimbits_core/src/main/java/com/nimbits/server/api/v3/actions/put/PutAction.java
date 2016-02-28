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

package com.nimbits.server.api.v3.actions.put;


import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.api.v3.actions.RestAction;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PutAction extends RestAction {



    public PutAction(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao) {
      super(entityService, valueService, userService, entityDao);
    }

    public void updateEntity(HttpServletRequest req, HttpServletResponse resp, User user) {
        String json = getContent(req);

        String path = req.getRequestURI();

        Action action = getAction(path);
        switch (action) {

            case me:
                putUser(resp, user, json);
                break;
            case root:
                putUser(resp, user, json);
                break;
            case snapshot:

                break;
            case series:

                break;
            case entity:

                putEntity(user, json);
                break;
        }
    }


    private void putEntity(User user, String content) {


        Entity entity = gson.fromJson(content, Entity.class);
        entityService.addUpdateEntity(valueService, user, entity);


    }

    private void putUser(HttpServletResponse resp, User user, String json)  {
        User update = gson.fromJson(json, UserModel.class);

        if (user.getIsAdmin()) {
            if (!StringUtils.isEmpty(update.getPassword())) {


                userService.updatePassword(update, update.getPassword());

            } else {

                entityService.addUpdateEntity(valueService, user, update);
            }
        }
        else {
            try {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (IOException ignored) {

            }
        }

    }



}
