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

package com.nimbits.server.api;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModel;
import com.nimbits.server.dao.pointDescription.EntityJPATransactionFactory;
import com.nimbits.server.dao.server.ServerTransactionFactory;
import com.nimbits.server.gson.GsonFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/15/11
 * Time: 1:47 PM
 */
public class EntityDescriptionServletImpl extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String serverJson = request.getParameter(Params.PARAM_SERVER);
        final String json = request.getParameter(Params.PARAM_ENTITY);
        final String action = request.getParameter(Params.PARAM_ACTION);

        final PrintWriter out = response.getWriter();


        out.println("Getting Post Data");
        out.println(action);
        if (StringUtils.isNotEmpty(json) && StringUtils.isNotEmpty(action)) {
            final Server server = GsonFactory.getInstance().fromJson(serverJson, ServerModel.class);
            final Server currentServer = ServerTransactionFactory.getInstance().readServer(server.getBaseUrl());

            final EntityDescription entityDescription;

                final Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
                final String desc = StringUtils.isEmpty(entity.getDescription()) ? entity.getName().getValue() : entity.getDescription();
               entity.setDescription(desc);
               entityDescription =
                        EntityModelFactory.createEntityDescription(
                                currentServer,entity
                        );




            if (action.equals(Action.update.name()) && StringUtils.isNotEmpty(serverJson)) {

                if (currentServer != null && entityDescription != null) {

                    if (  entity.getProtectionLevel().equals(ProtectionLevel.everyone) && sharedType(entity.getEntityType())) {

                        final EntityDescription retObj = EntityJPATransactionFactory.getInstance().addUpdateEntityDescription(entityDescription);

                        out.println("Reponse:");
                        String r = GsonFactory.getInstance().toJson(retObj);
                        out.println(r);
                    } else {
                        out.println("deleting : " + entityDescription.getUuid());
                        EntityJPATransactionFactory.getInstance().deleteEntityDescriptionByUUID(entityDescription.getUuid());
                    }


                }
            } else if (action.equals(Action.delete.name()) && entityDescription != null) {
                EntityJPATransactionFactory.getInstance().deleteEntityDescriptionByUUID(entityDescription.getUuid());


            }
        }
        out.close();
    }

    private boolean sharedType(EntityType type) {
        return type.equals(EntityType.point) ||  type.equals(EntityType.category) || type.equals(EntityType.file);

    }
}
