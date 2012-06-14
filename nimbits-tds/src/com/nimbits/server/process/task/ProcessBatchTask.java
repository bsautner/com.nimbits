/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.process.task;


import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.transactions.service.value.*;

import javax.jdo.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.math.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/28/11
 * Time: 1:39 PM
 */
@SuppressWarnings("unchecked")
public class ProcessBatchTask extends HttpServlet {
    private static final String P = "p";
    private static final String V = "v";
    private static final String T = "t";
    private static final String N = "n";
    private static final long serialVersionUID = 1L;
    private final Logger log = Logger.getLogger(ProcessBatchTask.class.getName());

    private Map<Long, BatchValue> timestampValueMap;
    private List<Long> timestamps;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            processBatch(req, resp);
        } catch (IOException e) {
            LogHelper.logException(ProcessBatchTask.class, e);
        } catch (NimbitsException e) {
            log.info(e.getMessage());
        }

    }

    protected void processBatch(final ServletRequest req, final ServletResponse resp) throws IOException, NimbitsException {


        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final Enumeration<String> el = req.getParameterNames();

        while (el.hasMoreElements()) {

            final String s = el.nextElement();
            log.info(s + '=' + req.getParameter(s));
        }

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
                        LogHelper.log(this.getClass(), b.getPointName().getValue());
                        LogHelper.log(this.getClass(), u.getEmail().getValue());
                        final List<Entity> pointTmp =   EntityServiceFactory.getInstance().getEntityByName(u, b.getPointName(),EntityType.point) ;

                        if (! pointTmp.isEmpty()) {
                            point = (Point) pointTmp.get(0);
                            points.put(b.getPointName(), point);
                        }
                    }
                }
                if (point != null) {
                    try {
                        final Value v = ValueFactory.createValueModel(0.0, 0.0, b.getValue(), b.getTimestamp(), b.getNote(), ValueFactory.createValueData(""), AlertType.OK);

                        ValueServiceFactory.getInstance().recordValue(b.getU(), point, v);

                    } catch (NimbitsException ex) {

                        log.info(ex.getMessage());

                    } catch (JDOException e) {
                        LogHelper.logException(ProcessBatchTask.class, e);


                    }
                }
            }
        }
        resp.flushBuffer();
        resp.setContentLength(0);
    }

    private void processQueryString(final Enumeration<String> enumeration,
                                    final Map m,
                                    final User u) throws NimbitsException {



        final String parameterName = enumeration.nextElement();
        if (parameterName.startsWith(P) && (parameterName.length() == 2 || parameterName.length() == 3)) {


            final int x = Integer.valueOf((String) parameterName.subSequence(1, parameterName.length()));
            if (m.containsKey(V + x)) {
                getValuesFromParam(m, u, x);
            }
        }
    }

    private void getValuesFromParam(final Map m, final User u, final int x) throws NimbitsException {

        final String[] values = (String[]) m.get(V + x);
        final String valStr = values[0];
        double value;


        try {
            value = Double.valueOf(valStr);
        } catch (NumberFormatException e) {
            value = 0.0;
        }


        final String[] points = (String[]) m.get(P + x);
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(points[0], EntityType.point);
        String note = "";

        if (m.containsKey(N + x)) {
            final String[] notes = (String[]) m.get(N + x);
            note = notes[0];
        }


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
        final BatchValue b = new BatchValue(u, pointName, timestamp, value, note);
        timestampValueMap.put(timestamp.getTime(), b);
        timestamps.add(timestamp.getTime());
    }



    private static class BatchValue {
        private final String note;
        private final User u;
        private final EntityName pointName;
        private final Date timestamp;
        private final Double value;

        BatchValue(final User u, final EntityName pointName, final Date timestamp,
                   final Double value, final String valNote) {
            super();
            this.u = u;
            this.pointName = pointName;
            this.timestamp = new Date(timestamp.getTime());
            this.value = value;
            this.note = valNote;

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
