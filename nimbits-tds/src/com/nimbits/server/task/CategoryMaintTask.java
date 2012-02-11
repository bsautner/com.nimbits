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

import com.google.gson.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.pointcategory.*;
import com.nimbits.shared.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

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

            Entity userEntity = EntityModelFactory.createEntity(u);
            EntityTransactionFactory.getInstance(u).addUpdateEntity(userEntity);
            List<Category> categories = CategoryServiceFactory.getInstance().getCategories(u, true,true, false);
            for (Category c : categories) {
                if (Utils.isEmptyString(c.getUUID())) {
                    log.info("Fixing category " + c.getName().getValue());
                    c.setUUID(UUID.randomUUID().toString());
                    c.setDescription("");
                    c.setProtectionLevel(ProtectionLevel.onlyMe);
                    CategoryServiceFactory.getInstance().updateCategory(u, c);

                }
                else {
                    if (! c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY)) {
                        Entity entity = EntityModelFactory.createEntity(
                                c.getName(),
                                c.getDescription(),
                                EntityType.category,
                                c.getProtectionLevel(),
                                c.getUUID(),
                                u.getUuid(),
                                u.getUuid());
                        EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
                    }
                    if (c.getPoints() != null) {
                        for (Point p : c.getPoints()) {
                            ProtectionLevel protectionLevel;
                            if (p.isPublic()) {
                                protectionLevel = ProtectionLevel.everyone;
                            }
                            else {
                                protectionLevel = ProtectionLevel.onlyMe;
                            }
                            String parentUUID = c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY) ?
                                    u.getUuid() : c.getUUID();
                            Entity entity = EntityModelFactory.createEntity(
                                    p.getName(),
                                    p.getDescription(),
                                    EntityType.point,
                                    protectionLevel,
                                    p.getUUID(),
                                    parentUUID,
                                    u.getUuid());

                            EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
                        }
                    }
                    if (c.getDiagrams() != null) {
                        for (Diagram p : c.getDiagrams()) {
                            ProtectionLevel protectionLevel = ProtectionLevel.get(p.getProtectionLevel());

                            String parentUUID = c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY) ?
                                    u.getUuid() : c.getUUID();
                            Entity entity = EntityModelFactory.createEntity(
                                    p.getName(),
                                    "",
                                    EntityType.point,
                                    protectionLevel,
                                    p.getUuid(),
                                    parentUUID,
                                    u.getUuid());

                            EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
                        }
                    }

                }


            }

        }


    }


}

