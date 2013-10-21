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

package com.nimbits.server.api;


import com.google.common.collect.Range;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class SeriesApi extends ApiBase {
    public static final int LIMIT = 1000;
    public static final int DEFAULT_COUNT = 1000;
    public static final String CSV = "csv";
    public static final String JSON = "json";


    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException {
        setup(req, resp, false);
        final Type valueListType = new TypeToken<List<ValueModel>>() { }.getType();
        ValueService valueService = ValueServiceFactory.getInstance(engine, taskService);

        final String startDate = req.getParameter(Parameters.sd.getText());
        final String endDate = req.getParameter(Parameters.ed.getText());
        String format = req.getParameter(Parameters.format.getText());
        String segStr = req.getParameter(Parameters.seg.getText());
        final Range<Date> timespanRange;
        if (StringUtils.isEmpty(format)) {
            format = JSON;
        }


        if (!Utils.isEmptyString(startDate) && !Utils.isEmptyString(endDate)) {
            long sd = Long.valueOf(startDate);
            long ed = Long.valueOf(endDate);
            timespanRange = Range.closed(new Date(sd), new Date(ed));
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
            Entity entity = entitySample.get(0);
            if (timespanRange != null && Utils.isEmptyString(segStr) ) {
                valueSample = valueService.getSeries(entity, timespanRange);

            }
            else if (!Utils.isEmptyString(segStr) && timespanRange != null) {
                int seg = Integer.valueOf(segStr);
                Range<Integer> segment = Range.closed(seg, seg + LIMIT);

                valueSample = valueService.getDataSegment(entity, timespanRange, segment);

            } else {
                valueSample = valueService.getTopDataSeries(entity, count);
            }
            String json;
            if (format.equals(CSV))  {

                SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                StringBuilder builder = new StringBuilder();
                builder
                        .append(entitySample.get(0).getName().getValue())
                        .append("," + "\n");



                Collections.sort(valueSample, new Comparator<Value>() {
                    @Override
                    public int compare(Value a, Value b) {
                        return a.getTimestamp().getTime() > b.getTimestamp().getTime()
                                ? 1
                                : a.getTimestamp().getTime() < b.getTimestamp().getTime()
                                ? -1
                                : 0;
                    }
                });
                for (Value v : valueSample) {
                    builder.append(tsFormat.format(v.getTimestamp()))
                            .append(",").append(v.getDoubleValue()).append("\n");
                }
                json = (builder.toString());

            }
            else {
                json = GsonFactory.getInstance().toJson(valueSample, valueListType);

            }

            completeResponse(resp, json);
        }






    }


}

