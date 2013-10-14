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


import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.ValueData;
import com.nimbits.cloudplatform.client.model.value.impl.ValueDataModel;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import com.nimbits.cloudplatform.server.transactions.value.service.ValueServiceRpc;
import com.nimbits.cloudplatform.shared.Utils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/28/11
 * Time: 1:39 PM
 */
@SuppressWarnings("unchecked")
@Service("batchTask")
@Deprecated
public class ProcessBatchTask extends HttpServlet implements org.springframework.web.HttpRequestHandler {
    private static final String P = "p";
    private static final String V = "v";
    private static final String T = "t";
    private static final String N = "n";
    private static final String LT = "lt";
    private static final String LN = "ln";
    private static final String DX = "dx";
    private static final String J = "j";
    private static final long serialVersionUID = 2L;
    private static final Logger log = Logger.getLogger(ProcessBatchTask.class.getName());

    private Map<Long, BatchValue> timestampValueMap;
    private List<Long> timestamps;
    private EntityService entityService;
    private ValueServiceRpc valueService;
    private final EntityService service = EntityServiceFactory.getInstance();

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            processBatch(req, resp);
        } catch (IOException e) {
            LogHelper.logException(ProcessBatchTask.class, e);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    protected void processBatch(final HttpServletRequest req, final ServletResponse resp) throws IOException, Exception {


        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final Enumeration<String> el = req.getParameterNames();


//        while (el.hasMoreElements()) {
//
//            final String s = el.nextElement();
//            log.info(s + '=' + req.getParameter(s));
//        }

        final User u = GsonFactory.getInstance().fromJson(userJson, UserModel.class);
        log.info(userJson);
        timestampValueMap = new HashMap<Long, BatchValue>(Const.CONST_MAX_BATCH_COUNT);
        timestamps = new ArrayList<Long>(Const.CONST_MAX_BATCH_COUNT);

        final Enumeration enumeration = req.getParameterNames();
        final Map m = req.getParameterMap();

        final Map<EntityName, Point> points = new HashMap<EntityName, Point>(Const.CONST_MAX_BATCH_COUNT);

        if (u != null) {
            while (enumeration.hasMoreElements()) {
                processQueryString(enumeration, m, u);
            }
            Collections.sort(timestamps);

            for (final long l : timestamps) {

                BatchValue b = timestampValueMap.get(l);
                Point point = null;
                if (b != null) {
                    if (points.containsKey(b.getPointName())) {
                        point = points.get(b.getPointName());

                    } else {

                        final List<Entity> pointTmp = service.getEntityByName(u, b.getPointName(), EntityType.point);

                        if (!pointTmp.isEmpty()) {
                            point = (Point) pointTmp.get(0);
                            points.put(b.getPointName(), point);
                        }
                    }
                }
                if (point != null) {
                    try {


                        if (b.getValues().isEmpty()) {
                            final ValueData data = ValueDataModel.getInstance(SimpleValue.getInstance(b.getData()));
                            final Value v = ValueFactory.createValueModel(b.getLocation(), b.getValue(), b.getTimestamp(), b.getNote(), data, AlertType.OK);
                            ValueServiceFactory.getInstance().recordValue(b.getU(), point, v);
                        } else {
                            ValueServiceFactory.getInstance().recordValues(b.getU(), point, b.getValues());
                        }

                        //  reportLocation(req, point);
                    } catch (Exception ex) {

                        log.info(ex.getMessage());

                    }
                }
            }
        }

        resp.flushBuffer();
        resp.setContentLength(0);
    }

    //    protected static void reportLocation(HttpServletRequest req, Entity entity) {
//        final String gps = req.getHeader("X-AppEngine-CityLatLong");
//        if (!Utils.isEmptyString(gps)) {
//            log.info("Reporting Location: " + gps);
//            LocationReportingHelperFactory.getInstance().reportLocation(entity, gps);
//        }
//    }
    private void processQueryString(final Enumeration<String> enumeration,
                                    final Map m,
                                    final User u) {


        final String parameterName = enumeration.nextElement();
        if (parameterName.startsWith(P) && (parameterName.length() == 2 || parameterName.length() == 3)) {


            final int x = Integer.valueOf((String) parameterName.subSequence(1, parameterName.length()));

            getValuesFromParam(m, u, x);

        }
    }

    private void getValuesFromParam(final Map m, final User u, final int x) {


        final double value = getDoubleFromMap(m, V + x);
        final double lat = getDoubleFromMap(m, LT + x);
        final double lng = getDoubleFromMap(m, LN + x);
        Location location = LocationFactory.createLocation(lat, lng);
        final String data = getStringFromMap(m, DX + x);
        final String[] points = (String[]) m.get(P + x);
        final EntityName pointName = CommonFactory.createName(points[0], EntityType.point);
        final String note = getStringFromMap(m, N + x);
        final Date timestamp = getDateFromMap(m, x);
        final List<Value> values = geValuesFromMap(m, J + x);
        final BatchValue b = new BatchValue(u, pointName, timestamp, value, note, data, location, values);
        timestampValueMap.put(timestamp.getTime(), b);
        timestamps.add(timestamp.getTime());
    }

    private List<Value> geValuesFromMap(Map map, String param) {

        Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();


        String json = getStringFromMap(map, param);
        if (!Utils.isEmptyString(json)) {
            List<Value> result = GsonFactory.getInstance().fromJson(json, valueListType);
            return result;

        } else {
            return Collections.emptyList();
        }

    }

    private Date getDateFromMap(Map m, int x) {
        Date timestamp;
        if (m.containsKey(T + x)) {
            final String[] timestampArray = (String[]) m.get(T + x);
            final String valTime = timestampArray[0];
            final BigDecimal time = new BigDecimal(valTime);

            timestamp = new Date(time.longValue());
        } else {
            timestamp = new Date();
        }
        while (timestampValueMap.containsKey(timestamp.getTime())) {
            timestamp = new Date(timestamp.getTime() + 1);
        }
        return timestamp;
    }

    private String getStringFromMap(Map map, String param) {

        if (map.containsKey(param)) {
            final String[] values = (String[]) map.get(param);
            return values[0];

        } else {
            return "";
        }

    }

    private double getDoubleFromMap(Map map, String param) {

        if (map.containsKey(param)) {
            final String[] values = (String[]) map.get(param);
            final String valStr = values[0];
            try {
                return Double.valueOf(valStr);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else {
            return 0.0;
        }

    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public void setValueService(ValueServiceRpc valueService) {
        this.valueService = valueService;
    }

    public ValueServiceRpc getValueService() {
        return valueService;
    }

    private static class BatchValue {
        private final String note;
        private final User u;
        private final EntityName pointName;
        private final Date timestamp;
        private final double value;
        private final String data;
        private final List<Value> values;
        private final Location location;

        BatchValue(final User u,
                   final EntityName pointName,
                   final Date timestamp,
                   final Double value,
                   final String valNote,
                   final String data,
                   final Location location,
                   final List<Value> values) {
            super();
            this.u = u;
            this.pointName = pointName;
            this.timestamp = new Date(timestamp.getTime());
            this.value = value;
            this.note = valNote;
            this.location = location;
            this.data = data;
            this.values = values;

        }

        public List<Value> getValues() {
            return values;
        }

        public String getData() {
            return data;
        }

        public Location getLocation() {
            return location;
        }

        public String getNote() {
            return note;
        }

        public User getU() {
            return u;
        }

        public EntityName getPointName() {
            return pointName;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public Double getValue() {
            return value;
        }
    }
}
