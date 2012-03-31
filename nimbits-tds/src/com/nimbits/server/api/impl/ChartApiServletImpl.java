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

package com.nimbits.server.api.impl;

import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.Path;
import com.nimbits.client.constants.Words;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.time.TimespanServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ChartApiServletImpl extends ApiServlet {
    private static final Logger log = Logger.getLogger(ChartApiServletImpl.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String autoscaleCode = "&chds=a";
    private static final String chartDateCode = "&chd=t:";


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String formatParam = req.getParameter(Parameters.format.getText());



        final Timespan timespan;
        try {
            init(req, resp, getContentType(formatParam));
            timespan = getTimestamp(req);



            final ExportType type = getContentType(formatParam);
            log.info(req.getQueryString());
            log.info(req.getParameter(Parameters.email.getText()));

            final boolean doScale = (!Utils.isEmptyString(getParam(Parameters.autoscale)) && getParam(Parameters.autoscale).equals(Words.WORD_TRUE));

            if (user==null)  {
                log.severe("Null user in chart api");
            }
            else {
                final List<EntityName> pointList = createPointList(getParam(Parameters.points), getParam(Parameters.point));
                int count = Utils.isEmptyString(getParam(Parameters.count)) ? 10 : Integer.valueOf(getParam(Parameters.count));

                if (type == ExportType.png) {
                    final String params = generateImageChartParams(req, timespan, count, doScale, user, pointList);
                    sendChartImage(resp, params);
                }


            }
        } catch (IOException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(e));
            }

        } catch (NimbitsException e) {
            if (user != null) {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(e));
            }

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
        Entity e;
        Point p;
        List<Value> values;
        for (final EntityName pointName : pointList) {


            e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            p = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

            if (p != null) {
                //Entity e = EntityServiceFactory.getInstance().getEntityByUUID(p.getUUID());
                if (e.getProtectionLevel().equals(ProtectionLevel.everyone) || !u.isRestricted()) {


                    values = (timespan != null) ?

                            RecordedValueServiceFactory.getInstance().getDataSegment(p, timespan) :
                            RecordedValueServiceFactory.getInstance().getTopDataSeries(p, valueCount).getValues();


                    for (final Value v : values) {
                        params.append(v.getDoubleValue()).append(Const.DELIMITER_COMMA);

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
        final URL url = new URL(Path.PATH_GOOGLE_CHART_API);
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

    private List<EntityName> createPointList(final String pointsListParam, final String pointParamName) throws NimbitsException {
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
        String startDate = req.getParameter(Parameters.sd.getText());
        String endDate = req.getParameter(Parameters.ed.getText());
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
