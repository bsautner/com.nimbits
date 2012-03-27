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

package com.nimbits.server.task;

import com.google.gson.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.point.*;
import com.nimbits.server.value.*;

import javax.jdo.*;
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
public class ProcessBatchTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ProcessBatchTask.class.getName());

    private Map<Long, BR> ht;
    private List<Long> ts;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            processBatch(req, resp);
        } catch (IOException e) {
            log.severe(e.getMessage());
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }

    private void processBatch(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, NimbitsException {

        final Gson gson = GsonFactory.getInstance();
        final String userJson = req.getParameter(Params.PARAM_JSON_USER);
        final User u = gson.fromJson(userJson, UserModel.class);


        log.info(u.getEmail().getValue());
        ht = new HashMap<Long, BR>();
        ts = new ArrayList<Long>();

        final Enumeration enumeration = req.getParameterNames();
        final Map m = req.getParameterMap();

        final Map<EntityName, Entity> points = new HashMap<EntityName, Entity>();

        if (u != null) {
            while (enumeration.hasMoreElements()) {
                //noinspection unchecked
                processQueryString(enumeration, m, u);
            }
            Entity entity;
            Collections.sort(ts);

            for (final long l : ts) {
                final BR b = ht.get(l);
                if (points.containsKey(b.pointName)) {
                    entity = points.get(b.pointName);

                } else {
                    entity = EntityServiceFactory.getInstance().getEntityByName(u, b.pointName);
                   //

                    if (entity != null) {
                        points.put(b.pointName, entity);
                    }
                }
                if (entity != null) {
                    try {
                        final Value v = ValueModelFactory.createValueModel(0.0, 0.0, b.value, b.timestamp, entity.getEntity(), b.note);
                        Point p = PointServiceFactory.getInstance().getPointByUUID(entity.getEntity());
                        RecordedValueServiceFactory.getInstance().recordValue(b.u, p, v, false);
                    } catch (JDOException e) {

                        log.severe(UserMessages.ERROR_BATCH_SERVICE_JDO + e.getMessage());

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

        int x;

        final String parameterName = enumeration.nextElement();
        if (parameterName.startsWith("p") && (parameterName.length() == 2 || parameterName.length() == 3)) {


            x = Integer.valueOf((String) parameterName.subSequence(1, parameterName.length()));
            if (m.containsKey("v" + x)) {
                getValuesFromParam(m, u, x);
            }
        }
    }

    private void getValuesFromParam(final Map m, final User u, final int x) throws NimbitsException {

        final String[] values = (String[]) m.get("v" + x);
        final String valStr = values[0];
        double value;


        try {
            value = Double.valueOf(valStr);
        } catch (NumberFormatException e) {
            value = 0.0;
        }


        Date timestamp;
        final String[] points = (String[]) m.get("p" + x);
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(points[0], EntityType.point);
        String note = "";

        if (m.containsKey("n" + x)) {
            final String[] notes = (String[]) m.get("n" + x);
            note = notes[0];
        }

        if (m.containsKey("t" + x)) {
            final String[] timestamps = (String[]) m.get("t" + x);
            final String valTime = timestamps[0];
            final BigDecimal time = new BigDecimal(valTime);

            timestamp = new Date(time.longValue());
        } else {
            timestamp = new Date();
        }
        while (ht.containsKey(timestamp.getTime())) {
            timestamp = new Date(timestamp.getTime() + 1);
        }
        final BR b = new BR(u, pointName, timestamp, value, note);
        ht.put(timestamp.getTime(), b);
        ts.add(timestamp.getTime());
    }

    private static class BR {
        private final String note;
        private final User u;
        private final EntityName pointName;
        private final Date timestamp;
        private final Double value;

        public BR(final User u, final EntityName pointName, final Date timestamp,
                  final Double value, final String valNote) {
            super();
            this.u = u;
            this.pointName = pointName;
            this.timestamp = timestamp;
            this.value = value;
            this.note = valNote;

        }


    }
}
