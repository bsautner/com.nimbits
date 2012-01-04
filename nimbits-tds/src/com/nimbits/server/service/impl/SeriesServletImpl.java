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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.service.impl;

import com.google.gwt.core.client.GWT;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.server.timespan.TimespanServiceFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public class SeriesServletImpl extends HttpServlet {
    private static final Logger log = Logger.getLogger(SeriesServletImpl.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        String result;

        int count;

        final String pointNameParam = req.getParameter(Const.PARAM_POINT);
        final String countStr = req.getParameter(Const.PARAM_COUNT);
        String segStr = req.getParameter(Const.PARAM_SEGMENT);
        final String startDate = req.getParameter(Const.PARAM_START_DATE);
        final String endDate = req.getParameter(Const.PARAM_END_DATE);

        Common.addResponseHeaders(resp, ExportType.plain);
        Timespan timespan = null;

        if (!Utils.isEmptyString(startDate) && !Utils.isEmptyString(startDate)) {
            try {
                timespan = TimespanServiceFactory.getInstance().createTimespan(startDate, endDate);
            } catch (NimbitsException e) {
                timespan = null;
            }
        }


        if (!Utils.isEmptyString(countStr)) {
            count = Integer.valueOf(countStr);
        } else {
            count = 10;

        }

        if (count > 1000) {
            count = 1000;
        }
        if (Utils.isEmptyString(segStr)) {
            segStr = "0";
        }

        User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
        } catch (NimbitsException e) {
            u = null;
        }

        try {
            final PrintWriter out = resp.getWriter();

            if (Utils.isEmptyString(pointNameParam)) {
                out.println("point name not specified");
            } else {


                final PointName pointName = CommonFactoryLocator.getInstance().createPointName(pointNameParam);
                final Point point = PointServiceFactory.getInstance().getPointByName(u, pointName);
                if (point == null) {
                    out.println("Point not found");
                } else {
                    final List<Value> values;
                    if (!Utils.isEmptyString(segStr) && timespan != null) {

                        int seg = Integer.valueOf(segStr);

                        values = RecordedValueServiceFactory.getInstance().getDataSegment(point, timespan, seg, seg + 1000);

                    } else {

                        values = RecordedValueServiceFactory.getInstance().getTopDataSeries(point, count).getValues();

                    }


                    result = GsonFactory.getInstance().toJson(values);//, GsonFactory.valueListType);
                    out.println(result);
                    out.close();
                }
            }

        } catch (IOException e) {
            GWT.log(e.getMessage(), e);

        } catch (NimbitsException e) {
            GWT.log(e.getMessage(), e);
        }
    }

}
