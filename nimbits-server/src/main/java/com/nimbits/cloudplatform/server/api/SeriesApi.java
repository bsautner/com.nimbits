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


import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.api.ApiBase;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 12/31/12
 * Time: 11:04 AM
 */

public class SeriesApi extends ApiBase {
    public static final int LIMIT = 1000;
    public static final int DEFAULT_COUNT = 1000;
    public static final String CSV = "csv";
    public static final String JSON = "json";
    public static final String ENTITY_NOT_FOUND = "Entity not found";

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws IOException, ServletException {
        setup(req, resp);
        final Type valueListType = new TypeToken<List<ValueModel>>() { }.getType();


        final String startDate = req.getParameter(Parameters.sd.getText());
        final String endDate = req.getParameter(Parameters.ed.getText());
        String format = req.getParameter(Parameters.format.getText());
        String segStr = req.getParameter(Parameters.seg.getText());
        final Range<Long> timespanRange;
        if (StringUtils.isEmpty(format)) {
            format = JSON;
        }


        if (!Utils.isEmptyString(startDate) && !Utils.isEmptyString(endDate)) {
            long sd = Long.valueOf(startDate);
            long ed = Long.valueOf(endDate);
            timespanRange = Range.between(sd, ed);
        } else {
            timespanRange = null;
        }


        int count = Utils.isEmptyString(req.getParameter(Parameters.count.getText())) ? DEFAULT_COUNT : Integer.valueOf(req.getParameter(Parameters.count.getText()));

        if (count > LIMIT) {
            count = LIMIT;
        }

        //
        if (user != null) {

            List<Entity> entitySample = getEntity(user, req, resp);


            List<Value> valueSample;
            final PrintWriter out = resp.getWriter();
            if (timespanRange != null && Utils.isEmptyString(segStr) ) {
                valueSample = ValueTransaction.getSeries(entitySample.get(0), timespanRange);

            }
            else if (!Utils.isEmptyString(segStr) && timespanRange != null) {
                int seg = Integer.valueOf(segStr);
                Range segment = Range.between(seg, seg + LIMIT);

                valueSample = ValueTransaction.getDataSegment(entitySample.get(0), timespanRange, segment);

            } else {
                valueSample = ValueTransaction.getTopDataSeries(entitySample.get(0), count);
            }

            if (format.equals(CSV))  {
                SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                StringBuilder builder = new StringBuilder();
                builder
                        .append(entitySample.get(0).getName().getValue())
                        .append("," + "Y1\n");
                for (Value v : valueSample) {
                    builder.append(tsFormat.format(v.getTimestamp()))
                            .append(",").append(v.getDoubleValue()).append("\n");

                }
                out.print(builder.toString());

            }
            else {
                String json = GsonFactory.getInstance().toJson(valueSample, valueListType);
                out.print(json);
            }

            out.close();
            resp.setStatus(HttpServletResponse.SC_OK);
            out.close();
        }






    }


}

