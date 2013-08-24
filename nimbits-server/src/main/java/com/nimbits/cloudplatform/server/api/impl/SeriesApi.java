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
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 12/31/12
 * Time: 11:04 AM
 */

@Service("seriesApi")
public class SeriesApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {
    public static final int LIMIT = 1000;

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        doGet(req, resp);

    }

    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws IOException {

        Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();



            doInit(req, resp, ExportType.json);


            final String startDate = req.getParameter(Parameters.sd.getText());
            final String endDate = req.getParameter(Parameters.ed.getText());
            String segStr = req.getParameter(Parameters.seg.getText());

            final Range<Long> timespanRange;

            if (!Utils.isEmptyString(startDate) && !Utils.isEmptyString(endDate)) {
                long sd = Long.valueOf(startDate);
                long ed = Long.valueOf(endDate);
                timespanRange = Range.between(sd, ed);
            } else {
                timespanRange = null;
            }


            int count = Utils.isEmptyString(getParam(Parameters.count)) ? 10 : Integer.valueOf(getParam(Parameters.count));

            if (count > LIMIT) {
                count = LIMIT;
            }
            if (Utils.isEmptyString(segStr)) {
                segStr = "0";
            }
            List<Value> sample;
            if (user != null && !user.isRestricted()) {


                List<Entity> entitySample = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.id), EntityType.point);
                if (entitySample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
                if (entitySample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

                } else {
                    final PrintWriter out = resp.getWriter();
                    if (!Utils.isEmptyString(segStr) && timespanRange != null) {
                        int seg = Integer.valueOf(segStr);
                        Range segment = Range.between(seg, seg + LIMIT);
                        sample = ValueTransaction.getDataSegment(entitySample.get(0), timespanRange, segment);

                    } else {
                        sample = ValueTransaction.getTopDataSeries(entitySample.get(0), count);
                    }
                    String json = GsonFactory.getInstance().toJson(sample, valueListType);
                    out.print(json);
                    out.close();
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.close();
                }


            }



    }


}

