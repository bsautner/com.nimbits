package com.nimbits.server.service;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.impl.CategoryModel;
import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
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
        final String serverJson = request.getParameter(Const.PARAM_SERVER);
        final String json = request.getParameter(Const.PARAM_ENTITY);
        final String action = request.getParameter(Const.PARAM_ACTION);
        final String entityTypeParam = request.getParameter(Const.PARAM_ENTITY_TYPE);
        final PrintWriter out = response.getWriter();


        out.println("Getting Post Data");
        out.println(action);
        if (StringUtils.isNotEmpty(json) && StringUtils.isNotEmpty(action)) {
            final Server server = GsonFactory.getInstance().fromJson(serverJson, ServerModel.class);
            final Server currentServer = ServerTransactionFactory.getInstance().readServer(server.getBaseUrl());
            boolean isPublic = false;

            final EntityDescription entityDescription;
            EntityType type = EntityType.get(Integer.valueOf(entityTypeParam));
            if (type.equals(EntityType.point)) {
                final Point point = GsonFactory.getInstance().fromJson(json, PointModel.class);
                final String desc = StringUtils.isEmpty(point.getDescription()) ? point.getName().getValue() : point.getDescription();
                entityDescription =
                        EntityModelFactory.createEntityDescription(
                                currentServer, point.getName(), point.getUUID(), desc, type
                        );
                isPublic = point.isPublic();

            } else if (type.equals(EntityType.category)) {
                final Category category = GsonFactory.getInstance().fromJson(json, CategoryModel.class);
                final String desc = StringUtils.isEmpty(category.getDescription()) ? category.getName().getValue() : category.getDescription();
                entityDescription =
                        EntityModelFactory.createEntityDescription(
                                currentServer, category.getName(), category.getUUID(), desc, type
                        );
                isPublic = category.getProtectionLevel().equals(ProtectionLevel.everyone);

            } else {
                entityDescription = null;
            }

            if (action.equals(Action.update.name()) && StringUtils.isNotEmpty(serverJson)) {

                if (currentServer != null && entityDescription != null) {

                    if (isPublic) {

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
}
