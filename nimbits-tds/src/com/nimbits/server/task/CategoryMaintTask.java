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

package com.nimbits.server.task;

import com.google.gson.Gson;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class CategoryMaintTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CategoryMaintTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final Gson gson = GsonFactory.getInstance();
        resp.setContentType(Const.CONTENT_TYPE_HTML);

        final String j = req.getParameter(Const.PARAM_USER);
        final User u = gson.fromJson(j, UserModel.class);


        if (u != null) {

//            Entity userEntity = EntityModelFactory.createEntity(u);
//            EntityTransactionFactory.getInstance(u).addUpdateEntity(userEntity);
//            List<Category> categories = CategoryServiceFactory.getInstance().getCategories(u, true,true, false);
//            for (Category c : categories) {
//                if (Utils.isEmptyString(c.getUuid())) {
//                    log.info("Fixing category " + c.getName().getValue());
//                    c.setUuid(UUID.randomUUID().toString());
//                    c.setDescription("");
//                    c.setProtectionLevel(ProtectionLevel.onlyMe);
//                    CategoryServiceFactory.getInstance().updateCategory(u, c);
//
//                }
//                else {
//                    if (! c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY)) {
//                        Entity entity = EntityModelFactory.createEntity(
//                                c.getName(),
//                                c.getDescription(),
//                                EntityType.category,
//                                c.getProtectionLevel(),
//                                c.getUuid(),
//                                u.getUuid(),
//                                u.getUuid());
//                        EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
//                    }
//                    if (c.getPoints() != null) {
//                        for (Point p : c.getPoints()) {
//                            ProtectionLevel protectionLevel;
//                            if (p.isPublic()) {
//                                protectionLevel = ProtectionLevel.everyone;
//                            }
//                            else {
//                                protectionLevel = ProtectionLevel.onlyMe;
//                            }
//                            String parentUUID = c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY) ?
//                                    u.getUuid() : c.getUuid();
//                            Entity entity = EntityModelFactory.createEntity(
//                                    p.getName(),
//                                    p.getDescription(),
//                                    EntityType.point,
//                                    protectionLevel,
//                                    p.getUuid(),
//                                    parentUUID,
//                                    u.getUuid());
//
//                            EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
//                        }
//                    }

                    //TODO
//                    if (c.getDiagrams() != null) {
//                        for (Diagram p : c.getDiagrams()) {
//                            ProtectionLevel protectionLevel = ProtectionLevel.get(p.getProtectionLevel());
//
//                            String parentUUID = c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY) ?
//                                    u.getUuid() : c.getUuid();
//                            Entity entity = EntityModelFactory.createEntity(
//                                    p.getName(),
//                                    "",
//                                    EntityType.point,
//                                    protectionLevel,
//                                    p.getUuid(),
//                                    parentUUID,
//                                    u.getUuid());
//
//                            EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
//                        }
//                    }

                }


            }

        }







