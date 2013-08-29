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

import javax.servlet.ServletException;
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

public class ValueApi extends ApiBase {

    public static final String ENTITY_NOT_FOUND = "Entity Not Found";
    public static final String MESSAGE = "The data point does not have any values recorded to it yet";
    final static Logger log = Logger.getLogger(ValueApi.class.getName());



    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
       setup(req, resp);
        final PrintWriter out = resp.getWriter();
        String json = req.getParameter(Parameters.json.getText());
        if (Utils.isEmptyString(json)) {
            json = getContent(req);
        }
        if (user != null && ! Utils.isEmptyString(json)) {
            List<Entity> entitySample = getEntity(user, req, resp);

            if (entitySample.isEmpty()) {
               sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ENTITY_NOT_FOUND);
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
        }

    }



    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException, IOException {


        setup(req, resp);




        if (user != null) {

            List<Entity> entitySample = getEntity(user, req, resp);

                List<Value> sample = ValueTransaction.getCurrentValue(entitySample.get(0));
                if (sample.isEmpty()) {
                    sendError(resp, HttpServletResponse.SC_NO_CONTENT, MESSAGE);
                } else {
                    final PrintWriter out = resp.getWriter();
                    String json = GsonFactory.getInstance().toJson(sample.get(0), ValueModel.class);
                    out.print(json);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.close();
                }



        }




    }



}