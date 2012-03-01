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

package com.nimbits.server.service;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.server.dao.pointDescription.EntityJPATransactionFactory;
import com.nimbits.server.dao.search.*;
import com.nimbits.server.gson.GsonFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/15/11
 * Time: 5:07 PM
 */
public class EntityDescriptionSearchServletImpl extends HttpServlet {
    //todo make safer
    private String safeSearchText(final String search) {
        if (search.length() > 0
                && search.length() < 200
                && !search.contains(";")
                && !search.contains("--")
                && !search.contains("(")
                && !search.contains(")")
                && !search.contains("{")
                && !search.contains("}")) {
            return search;
        } else {
            return "nimbits data";
        }


    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {

        resp.setContentType(Const.CONTENT_TYPE_PLAIN);
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");


        final String dangerousSearchText = request.getParameter(Const.PARAM_SEARCH);
        final String format = request.getParameter(Const.Params.PARAM_FORMAT);
        final ExportType type = StringUtils.isEmpty(format) ?
             ExportType.json :  ExportType.valueOf(format);



        final String safeSearch = safeSearchText(dangerousSearchText);
        try {
            SearchLogTransactionFactory.getInstance().addUpdateSearchLog(dangerousSearchText);
        } catch (NimbitsException ignored) {

        }
        final List<EntityDescription> result = EntityJPATransactionFactory.getInstance().searchEntityDescription(safeSearch);
        final PrintWriter out = resp.getWriter();
        String r;
        if (format.equals(ExportType.json)) {
            r = GsonFactory.getInstance().toJson(result, GsonFactory.pointDescriptionListType);
        } else {
            StringBuilder sb = new StringBuilder();

            for (EntityDescription d : result) {
                String img;

                if (d.getEntityType().equals(EntityType.category)) {
                    img = "<img src=\"http://www.nimbits.com/images/folder.png\" width=30 height=30>";

                } else {
                    img = "<img align=left src=\"http://www.nimbits.com/images/ball.png\" width=30 height=30>";
                }

                sb.append("<div class=\"row\">")
                        .append("<h5>").append("<a href=\"").append(d.getServer().getBaseUrl()).append("/report.html?uuid=").append(d.getUuid())
                        .append("\" target=\"_blank\">").append(d.getName()).append("</a></h5>")
                        .append(img)
                        .append("<p>").append(d.getDesc()).append("</p>")
                        .append("</div>");
            }
            r = "<div class=\"row\"></div>";
            out.print(sb.toString());
        }


        out.close();


    }
}
