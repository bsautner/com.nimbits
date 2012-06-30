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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.time.TimespanServiceFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.feed.FeedServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

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
            doInit(req, resp, ExportType.plain);


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


            int count = Utils.isEmptyString(getParam(Parameters.count)) ? 10 : Integer.valueOf(getParam(Parameters.count));

            if (count > 1000) {
                count = 1000;
            }
            if (Utils.isEmptyString(segStr)) {
                segStr = "0";
            }


            String name = getParam(Parameters.point);
            if (Utils.isEmptyString(name)) {
                name = getParam(Parameters.name);
            }


            final PrintWriter out = resp.getWriter();

            if (Utils.isEmptyString(name)) {
                resp.setStatus(Const.HTTP_STATUS_BAD_REQUEST);
            } else {


                final EntityName pointName = CommonFactoryLocator.getInstance().createName(name, EntityType.point);
                List<Entity> points = EntityServiceFactory.getInstance().getEntityByName(user, pointName,EntityType.point);
                if (! points.isEmpty()) {
                     Point point = (Point) points.get(0);

                    // final Point point = (Point) EntityServiceFactory.getInstance().getEntityByKey(e.getKey(), PointEntity.class.getName());
                    if (point == null) {

                        resp.setStatus(Const.HTTP_STATUS_BAD_REQUEST);
                    } else {
                        final List<Value> values;
                        if (!Utils.isEmptyString(segStr) && timespan != null) {

                            int seg = Integer.valueOf(segStr);

                            values = ValueServiceFactory.getInstance().getDataSegment(point, timespan, seg, seg + 1000);

                        } else {

                            values = ValueServiceFactory.getInstance().getTopDataSeries(point, count);

                        }


                        String result = GsonFactory.getInstance().toJson(values);
                        out.println(result);
                        resp.setStatus(Const.HTTP_STATUS_OK);
                        out.close();
                    }
                }
                else {
                    resp.setStatus(Const.HTTP_STATUS_BAD_REQUEST);
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
