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
import com.nimbits.client.exception.*;


import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import com.nimbits.server.com.nimbits.server.transactions.dao.instance.InstanceTransactions;
import com.nimbits.server.com.nimbits.server.transactions.dao.search.SearchLogTransactions;
import com.nimbits.server.gson.GsonFactory;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
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

    private SearchLogTransactions transactions;
    private EntityJPATransactions entityTransactions;

    @Resource(name="searchDao")
    public void setInstanceTransactions(SearchLogTransactions transactions) {
        this.transactions = transactions;
    }

    @Resource(name="entityDao")
    public void setEntityTransactions(EntityJPATransactions transactions) {
        this.entityTransactions = transactions;
    }



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


        final String dangerousSearchText = request.getParameter(Parameters.search.getText());
        final String format = request.getParameter(Parameters.format.getText());
        final ExportType type = StringUtils.isEmpty(format) ?
             ExportType.json :  ExportType.valueOf(format);



        final String safeSearch = safeSearchText(dangerousSearchText);
        try {
            transactions.addUpdateSearchLog(dangerousSearchText);
        } catch (NimbitsException ignored) {

        }
        final List<Entity> result = entityTransactions.searchEntity(safeSearch);
        final PrintWriter out = resp.getWriter();
        String r;
        if (format.equals(ExportType.json)) {
            r = GsonFactory.getInstance().toJson(result, GsonFactory.pointDescriptionListType);
        } else {
            StringBuilder sb = new StringBuilder();

            for (Entity d : result) {
                String img;

                if (d.getEntityType().equals(EntityType.category)) {
                    img = "<img src=\"http://www.nimbits.com/images/folder.png\" width=30 height=30>";

                } else {
                    img = "<img align=left src=\"http://www.nimbits.com/images/ball.png\" width=30 height=30>";
                }

                try {
                    sb.append("<div class=\"row\">")
                            .append("<h5>").append("<a href=\"").append(d.getInstance().getBaseUrl()).append("/report.html?uuid=").append(d.getKey())
                            .append("\" target=\"_blank\">").append(d.getName()).append("</a></h5>")
                            .append(img)
                            .append("<p>").append(d.getDescription()).append("</p>")
                            .append("</div>");
                } catch (NimbitsException e) {

                }
            }
            r = "<div class=\"row\"></div>";
            out.print(sb.toString());
        }


        out.close();


    }
}
