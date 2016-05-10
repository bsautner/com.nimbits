/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api;


import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * The Series Service allows you to POST large volumes of data to many Data Points and the GET a series for a single data point.
 * You can download series data in chunks, optionally specifying a time range, max count or segment of data.  If you have millions of
 * values to download, it's not practical to try and download them all at once.  You can download 24 hours of data at a time
 * and assemble them on your client.
 */

@Service
public class SeriesApi extends ApiBase {

    private static final int LIMIT = 1000;
    private static final String CSV = "csv";
    private static final String CSV_UT = "csv-ut";
    private static final String JSON = "json";

    private static final Logger logger = Logger.getLogger(SeriesApi.class.getName());


    @Autowired
    private ValueService valueService;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private BlobStore blobStore;

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException {


        initRequest(req, resp);
        final User user = (User) req.getAttribute(Parameters.user.getText());
        final Gson gson =  GsonFactory.getInstance(true);


        final Type valueListType = new TypeToken<List<Value>>() {
        }.getType();
        final String startDate = req.getParameter(Parameters.sd.getText());
        final String endDate = req.getParameter(Parameters.ed.getText());
        final String maskParam = req.getParameter(Parameters.mask.getText());
        final String segStr = req.getParameter(Parameters.seg.getText());
        final String format = setFormat(req);

        final Optional<Range<Date>> timespan = getDateRange(startDate, endDate);
        final String countParam = req.getParameter(Parameters.count.getText());


        Optional<String> mask = StringUtils.isEmpty(maskParam) ? Optional.<String>absent() : Optional.of(maskParam);

        Optional<Integer> count = (StringUtils.isEmpty(countParam)) ? Optional.<Integer>absent() : Optional.of(Integer.valueOf(countParam));


        try {
            if (user != null) {

                final Entity entity = getEntity(req, resp, user);


                List<Value> valueSample;
                Optional<Range<Integer>> range;
                if (count.isPresent()) {
                    range = Optional.of(Range.closed(0, count.get()));
                }
                else {
                    range = Optional.of(Range.closed(0, LIMIT));
                }
                valueSample = valueService.getSeries(blobStore, entity, timespan, range, mask);




                String json;
                if (format.equals(CSV)) {

                    SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    StringBuilder builder = new StringBuilder();
                    builder
                            .append(entity.getName().getValue())
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

                } else if (format.equals(CSV_UT)) {


                    StringBuilder builder = new StringBuilder();
                    builder
                            .append(entity.getName().getValue())
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
                        builder.append(v.getTimestamp().getTime())
                                .append(",").append(v.getDoubleValue()).append("\n");
                    }
                    json = (builder.toString());

                } else {
                    json = gson.toJson(valueSample, valueListType);

                }

                completeResponse(resp, json);

            }
        } catch (Throwable ex) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }


    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            initRequest(req, resp);
            User user = (User) req.getAttribute(Parameters.user.getText());
            final Type listType = new TypeToken<List<PointModel>>() {
            }.getType();

            Gson gson =  GsonFactory.getInstance(true);
            String json = req.getParameter(Parameters.json.getText());

            if (isEmpty(json)) {
                json = getContent(req);
            }

            if (user != null && StringUtils.isNotEmpty(json)) {

                List<Point> points = gson.fromJson(json, listType);
                Map<String, Point> completeMap = new HashMap<>(points.size());

                for (Point p : points) {
                    if (p != null && p.getValues() != null) {
                        String id = p.getId();
                        {

                            Point completePoint;

                            if (completeMap.containsKey(id)) {
                                completePoint = completeMap.get(id);
                            }
                            else {

                                 completePoint = (Point) entityDao.getEntity(user, id, EntityType.point).get();
                                 completeMap.put(id, completePoint);




                            }


                            if ( completePoint != null) {
                                valueService.recordValues(blobStore, user, completePoint, p.getValues());
                            }



                        }
                    } else {
                        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Point not found");
                    }
                }

                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                if (user == null) {
                    sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "invalid request, could not authenticate");
                }
                if (StringUtils.isEmpty(json)) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "missing json parameter");
                }
            }
        } catch (Throwable error) {
            logger.severe(ExceptionUtils.getStackTrace(error));
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error.getMessage());
        }
    }

    protected String setFormat(HttpServletRequest req) {
        String format = req.getParameter(Parameters.format.getText());
        if (isEmpty(format)) {
            format = JSON;
        }
        return format;
    }

    protected Optional<Range<Date>> getDateRange(String startDate, String endDate) {
        Optional<Range<Date>> timespanRange;
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            long sd = Long.valueOf(startDate);
            long ed = Long.valueOf(endDate);
            timespanRange = Optional.of(Range.closed(new Date(sd), new Date(ed)));
        } else {
            timespanRange = Optional.absent();
        }
        return timespanRange;
    }


}

