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

package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class ApiBase extends HttpServlet {

    protected String getContent(HttpServletRequest req)  {

        BufferedReader reader;
        try {
            reader = req.getReader();
            if (req.getContentLength() > 0) {
                StringBuilder jb = new StringBuilder(req.getContentLength());
                String line;
                while ((line = reader.readLine()) != null) {
                    jb.append(line);
                }


                return jb.toString();
            }
            else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    protected List<Entity> getEntity(final User user, final HttpServletRequest req, final HttpServletResponse resp) {
        List<Entity> sample = null;
        EntityType entityType;
        String id = req.getParameter(Parameters.id.getText());
        String uuid = req.getParameter(Parameters.uuid.getText());
        String type = req.getParameter(Parameters.type.getText());
        if (! StringUtils.isEmpty(type)) {
            entityType = EntityType.valueOf(type);
        }
        else {
            entityType = EntityType.point;
        }
        if (!Utils.isEmptyString(id)) {
            sample = EntityServiceImpl.getEntityByKey(user, id, entityType);
        }
        else  if (!Utils.isEmptyString(uuid)) {
            sample = EntityServiceImpl.getEntityByUUID(user, uuid, entityType);
        }
        else {
            resp.addHeader("error details", "you must supply an id or uuid for the entity");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        if (sample != null) {
            if (sample.isEmpty()) {
                resp.addHeader("error details", "entity not found");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return Collections.emptyList();

            } else {
                return sample;

            }

        }
        else {
            return Collections.emptyList();
        }

    }

    protected void addHeaders(HttpServletResponse resp) {
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");
    }
}
