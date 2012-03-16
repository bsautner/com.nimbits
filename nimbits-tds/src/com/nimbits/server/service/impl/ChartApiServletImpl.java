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

package com.nimbits.server.service.impl;

import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.timespan.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class ChartApiServletImpl extends HttpServlet {
    private static final Logger log = Logger.getLogger(ChartApiServletImpl.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String autoscaleCode = "&chds=a";
    private static final String chartDateCode = "&chd=t:";


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        log.info(req.getQueryString());
        log.info("Chart API Request");

        log.info(req.getParameter(Const.Params.PARAM_EMAIL));
        final String pointsListParam = req.getParameter(Const.Params.PARAM_POINTS);

        final String pointParamName = req.getParameter(Const.Params.PARAM_POINT);
        final String countParam = req.getParameter(Const.Params.PARAM_COUNT);
        final String autoScaleParam = req.getParameter(Const.Params.PARAM_AUTO_SCALE);
        final Timespan timespan;
        try {
            timespan = getTimestamp(req);

            final String formatParam = req.getParameter(Const.Params.PARAM_FORMAT);

            final ExportType type = getContentType(formatParam);
            log.info(req.getQueryString());
            log.info(req.getParameter(Const.Params.PARAM_EMAIL));
            Common.addResponseHeaders(resp, type);

            final boolean doScale = (!Utils.isEmptyString(autoScaleParam) && autoScaleParam.equals(Const.WORD_TRUE));
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

            if (u==null)  {
                log.severe("Null user in chart api");
            }
            else {
                final List<EntityName> pointList = createPointList(pointsListParam, pointParamName);
                int count = Utils.isEmptyString(countParam) ? 10 : Integer.valueOf(countParam);

                if (type == ExportType.png) {
                    final String params = generateImageChartParams(req, timespan, count, doScale, u, pointList);
                    sendChartImage(resp, params);
                }


            }
        } catch (IOException e) {
            log.severe("Chart API Request Error" + e.getMessage());

        } catch (NimbitsException e) {
            log.severe("Chart API Request Error" + e.getMessage());

        }
    }

    private ExportType getContentType(String formatParam) {
        ExportType type;

        if (Utils.isEmptyString(formatParam)) {
            type = ExportType.png;
        } else if (formatParam.equals("image")) {
            type = ExportType.png;
        } else if (formatParam.equals("table")) {
            type = ExportType.table;

        } else {
            type = ExportType.plain;
        }
        return type;
    }



    private String generateImageChartParams(final HttpServletRequest req, final Timespan timespan, final int valueCount, final boolean doScale, final User u, final List<EntityName> pointList) throws NimbitsException {

        StringBuilder params = new StringBuilder();
        params.append(req.getQueryString());
        params.append(chartDateCode);

        for (final EntityName pointName : pointList) {


            Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            final Point p = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

            if (p != null) {
                //Entity e = EntityServiceFactory.getInstance().getEntityByUUID(p.getUUID());
                if (e.getProtectionLevel().equals(ProtectionLevel.everyone) || !u.isRestricted()) {


                    final List<Value> values = (timespan != null) ?

                            RecordedValueServiceFactory.getInstance().getDataSegment(p, timespan) :
                            RecordedValueServiceFactory.getInstance().getTopDataSeries(p, valueCount).getValues();


                    for (final Value v : values) {
                        params.append(v.getNumberValue()).append(Const.DELIMITER_COMMA);

                    }
                    if (params.lastIndexOf(Const.DELIMITER_COMMA) > 0) {
                        params.deleteCharAt(params.lastIndexOf(Const.DELIMITER_COMMA));

                    }
                    params.append(Const.DELIMITER_BAR);

                }
            }


        }
        if (params.lastIndexOf(Const.DELIMITER_BAR) > 0) {
            params.deleteCharAt(params.lastIndexOf(Const.DELIMITER_BAR));
        }

        if (doScale) {
            params.append(autoscaleCode);
        }

        return params.toString();
    }

    private void sendChartImage(final HttpServletResponse resp, final String params) throws IOException {
        final URL url = new URL(Const.PATH_GOOGLE_CHART_API);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(Const.METHOD_POST);
        connection.setReadTimeout(Const.DEFAULT_HTTP_TIMEOUT);
        final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(params);
        writer.close();
        final InputStream is = connection.getInputStream();
        resp.setContentType(ExportType.png.getCode());
        final int length = connection.getContentLength();
        log.info(params);
        final OutputStream out = resp.getOutputStream();
        resp.setContentLength(length);
        byte[] buffer = new byte[length];
        for (int i; (i = is.read(buffer)) >= 0; ) {
            out.write(buffer, 0, i);
        }
        out.close();
    }

    private List<EntityName> createPointList(String pointsListParam, String pointParamName) throws NimbitsException {
        final List<EntityName> pointList = new ArrayList<EntityName>();
        if (!Utils.isEmptyString(pointParamName)) {
            pointList.add(CommonFactoryLocator.getInstance().createName(pointParamName, EntityType.point));
        } else if (!Utils.isEmptyString(pointsListParam)) {
            final String[] p1 = (pointsListParam.split(","));
            final List<String> pointsParams = Arrays.asList(p1);
            for (String pn : pointsParams) {
                pointList.add(CommonFactoryLocator.getInstance().createName(pn, EntityType.point));
            }
        }
        return pointList;
    }

    private Timespan getTimestamp(HttpServletRequest req) throws NimbitsException {
        Timespan timespan = null;
        String startDate = req.getParameter(Const.Params.PARAM_START_DATE);
        String endDate = req.getParameter(Const.Params.PARAM_END_DATE);
        //support for legacy st param
        if (startDate == null) {
            startDate = req.getParameter("st");
        }
        //support for legacy et param
        if (endDate == null) {
            endDate = req.getParameter("et");
        }

        if (startDate != null && endDate != null) {

            timespan = TimespanServiceFactory.getInstance().createTimespan(startDate, endDate);

        }
        return timespan;
    }

}
