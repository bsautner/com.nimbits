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

package com.nimbits.cloudplatform.server.process.task;

import com.google.gwt.core.client.GWT;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.service.ValueServiceRpc;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("deleteTask")

public class DeleteRecordedValuesTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;
    private ValueServiceRpc valueService;

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointJson = req.getParameter(Parameters.json.getText());
        Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);


        try {

                deleteData(point);

        } catch (NumberFormatException e) {
            GWT.log(e.getMessage());
        }
    }

     //TODO - delete blobs
    private void deleteData(final Point point)  {
        ValueServiceFactory.getInstance().deleteExpiredData(point);


    }

    public void setValueService(ValueServiceRpc valueService) {
        this.valueService = valueService;
    }

    public ValueServiceRpc getValueService() {
        return valueService;
    }
}
