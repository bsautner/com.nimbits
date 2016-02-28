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

package com.nimbits.server.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * GET a user's entire tree, with all points, calcs, triggers etc.
 */
public class TreeApi extends ApiBase {



    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException {


        Gson gInstance = GsonFactory.getInstance(true);


        final Type entityListType = new TypeToken<List<EntityModel>>() {
        }.getType();

        initRequest(req, resp);
        User user = (User) req.getAttribute(Parameters.user.getText());

        if (user != null ) {
            final List<Entity> sample = entityService.getEntities(user);

            if (req.getParameter(Parameters.format.getText()) == null) {


                final String json = gInstance.toJson(sample, entityListType);
                completeResponse(resp, json);
            } else if (req.getParameter(Parameters.format.getText()).equals("jit")) {

                List<JitModel> children = new ArrayList<>();
                for (Entity e : sample) {
                    if (e.getParent().equals(user.getEmail().getValue())) {
                        if (!e.getEntityType().equals(EntityType.user)) {
                            if (e.getEntityType().recordsData()) {

                                children.add(new JitModel(e, getCurrentValue(e), e.getKey(), e.getName().getValue(), new Object(), getChilden(sample, e)));
                            } else {

                                children.add(new JitModel(e, e.getKey(), e.getName().getValue(), new Object(), getChilden(sample, e)));
                            }
                        }

                    }
                }
                JitModel top = new JitModel(user, user.getKey(), user.getName().getValue(), new Object(), children);
                final String json =  GsonFactory.getInstance(true).toJson(top);
                completeResponse(resp, json);


            }

        }


    }

    private Value getCurrentValue(Entity e) {

        return valueService.getCurrentValue(blobStore, e);
    }

    private List<JitModel> getChilden(List<Entity> list, Entity parent) {
        List<JitModel> children = new ArrayList<>();
        for (Entity e : list) {
            if (e.getParent().equals(parent.getKey())) {
                if (e.getEntityType().recordsData()) {

                    children.add(new JitModel(e, getCurrentValue(e), e.getKey(), e.getName().getValue(), new Object(), getChilden(list, e)));
                } else {

                    children.add(new JitModel(e, e.getKey(), e.getName().getValue(), new Object(), getChilden(list, e)));
                }
            }

        }
        return children;
    }
}

