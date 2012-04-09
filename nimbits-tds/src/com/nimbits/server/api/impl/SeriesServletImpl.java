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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.time.TimespanServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class SeriesServletImpl extends ApiServlet {


    private static final long serialVersionUID = 1L;



    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {

        try {
        String result;
        doInit(req, resp, ExportType.plain);
        int count;


        String segStr = req.getParameter(Parameters.seg.getText());
        final String startDate = req.getParameter(Parameters.sd.getText());
        final String endDate = req.getParameter(Parameters.ed.getText());


        Timespan timespan = null;

        if (!Utils.isEmptyString(startDate) && !Utils.isEmptyString(endDate)) {
            try {
                timespan = TimespanServiceFactory.getInstance().createTimespan(startDate, endDate);
            } catch (NimbitsException e) {
                timespan = null;
            }
        }


            count = Utils.isEmptyString(getParam(Parameters.count)) ? 10 : Integer.valueOf(getParam(Parameters.count));

        if (count > 1000) {
            count = 1000;
        }
        if (Utils.isEmptyString(segStr)) {
            segStr = "0";
        }





            final PrintWriter out = resp.getWriter();

            if (Utils.isEmptyString(getParam(Parameters.point))) {
                out.println("point name not specified");
            } else {


                final EntityName pointName = CommonFactoryLocator.getInstance().createName(getParam(Parameters.point), EntityType.point);
                Point point = (Point) EntityServiceFactory.getInstance().getEntityByName(user, pointName,PointEntity.class.getName());

               // final Point point = (Point) EntityServiceFactory.getInstance().getEntityByKey(e.getKey(), PointEntity.class.getName());
                if (point == null) {
                    out.println("Point not found");
                } else {
                    final List<Value> values;
                    if (!Utils.isEmptyString(segStr) && timespan != null) {

                        int seg = Integer.valueOf(segStr);

                        values = RecordedValueServiceFactory.getInstance().getDataSegment(point, timespan, seg, seg + 1000);

                    } else {

                        values = RecordedValueServiceFactory.getInstance().getTopDataSeries(point, count);

                    }


                    result = GsonFactory.getInstance().toJson(values);
                    out.println(result);
                    out.close();
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

}
