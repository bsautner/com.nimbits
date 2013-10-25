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

package com.nimbits.server.process.task;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class PointMaintTask extends ApiBase {


    private ValueService valueService;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
        setup(req, resp);
        valueService  = ValueServiceFactory.getInstance(engine, taskService);
        processPost(req);


    }


    public void processPost(final HttpServletRequest req) throws IOException {


        final String j = req.getParameter(Parameters.json.getText());
        final Point entity = GsonFactory.getInstance().fromJson(j, PointModel.class);
        if (entity.getExpire() > 0) {
            taskService.startDeleteDataTask(
                    entity,
                    true, entity.getExpire());
        }
        valueService.consolidateBlobs(entity);
    }






}

