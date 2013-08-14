/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.api.impl;

import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

@Service("treeApi")
public class TreeApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        doGet(req, resp);

    }

    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws IOException {

        Type entityListType = new TypeToken<List<EntityModel>>() {
        }.getType();


        final PrintWriter out = resp.getWriter();

        try {
            doInit(req, resp, ExportType.json);


            if (user != null && !user.isRestricted()) {

                List<Entity> sample = EntityServiceImpl.getEntities(user);

                String json = GsonFactory.getInstance().toJson(sample, entityListType);
                out.print(json);
                resp.setStatus(HttpServletResponse.SC_OK);

            } else {
                // out.print(Words.WORD_FALSE);
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.close();

    }
}

