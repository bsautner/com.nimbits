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



public class DeleteRecordedValuesTask extends ApiBase {

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;

    private ValueService valueService;
    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException {
        setup(req, resp, false);
        valueService = ValueServiceFactory.getInstance(engine, taskService);
        final String pointJson = req.getParameter(Parameters.json.getText());
        Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);

        deleteData(point);

    }

    //TODO - delete blobs
    private void deleteData(final Point point)  {
        valueService.deleteExpiredData(point);


    }


}
