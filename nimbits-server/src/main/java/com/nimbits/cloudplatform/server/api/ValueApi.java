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
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Author: Benjamin Sautner
 * Date: 12/28/12
 * Time: 4:11 PM
 */

public class ValueApi extends HttpServlet {
    final Logger log = Logger.getLogger(ValueApi.class.getName());

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

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        addHeaders(resp);


        String json = req.getParameter(Parameters.json.getText());
        String id = req.getParameter(Parameters.id.getText());
        final User user = UserTransaction.getHttpRequestUser(req);
        final PrintWriter out = resp.getWriter();

        if (Utils.isEmptyString(json)) {
             json = getContent(req);
             id = req.getHeader(Parameters.id.getText());

        }


        if (user != null && !user.isRestricted() && ! Utils.isEmptyString(id) && ! Utils.isEmptyString(json)) {
            log.info(user.getEmail().getValue());
            List<Entity> entitySample = EntityServiceImpl.getEntityByKey(user, id, EntityType.point);
            if (entitySample.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {

                Value value = GsonFactory.getInstance().fromJson(json, ValueModel.class);
                if (value.getTimestamp().getTime() == 0) {
                    value = ValueModel.getInstance(value, new Date());
                }
                Value recorded = ValueTransaction.recordValue(user, entitySample.get(0), value);
                resp.setStatus(HttpServletResponse.SC_OK);
                String respString = GsonFactory.getInstance().toJson(recorded, ValueModel.class);
                out.print(respString);
                out.close();

            }


        } else {
            // out.print(Words.WORD_FALSE);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        }



    }



    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp)  {


        addHeaders(resp);

        try {
            final PrintWriter out = resp.getWriter();
           // doInit(req, resp, ExportType.json);
            final User user = UserTransaction.getHttpRequestUser(req);
            final String id = req.getParameter(Parameters.id.getText());
            if (user != null && !user.isRestricted()) {

                List<Entity> entitySample = EntityServiceImpl.getEntityByKey(user, id, EntityType.point);
                if (entitySample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    List<Value> sample = ValueTransaction.getCurrentValue(entitySample.get(0));
                    if (sample.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        String json = GsonFactory.getInstance().toJson(sample.get(0), ValueModel.class);
                        out.print(json);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }


                }


            } else {
                // out.print(Words.WORD_FALSE);
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }
            out.close();
        } catch (Exception e) {
            resp.addHeader("ERROR", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


        }


    }

    private void addHeaders(HttpServletResponse resp) {
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");
    }

}