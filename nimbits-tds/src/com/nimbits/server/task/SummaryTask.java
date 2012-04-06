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

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.point.*;
import com.nimbits.server.summary.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;
import org.apache.commons.math3.stat.descriptive.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:35 PM
 */
public class SummaryTask  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SummaryTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String json = req.getParameter(Parameters.json.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

        final User user;
        try {
            user = UserServiceFactory.getInstance().getUserByKey(entity.getOwner());

            final Summary summary = SummaryTransactionFactory.getInstance(user).readSummary(entity);
            final Date now = new Date();
            final long d = new Date().getTime() - summary.getSummaryIntervalMs();
            if (summary.getLastProcessed().getTime() < d) {

                try {
                   // final Point source = PointServiceFactory.getInstance().getPointByKey(summary.getEntity());
                    final Point source = (Point) EntityServiceFactory.getInstance().getEntityByKey(summary.getEntity(), PointEntity.class.getName());

                    final Timespan span = TimespanModelFactory.createTimespan(new Date(now.getTime() - summary.getSummaryIntervalMs()), now);
                    final List<Value> values;
                    values = RecordedValueServiceFactory.getInstance().getDataSegment(source, span);

                    final double[] doubles = new double[values.size()];
                    for (int i = 0; i< values.size(); i++) {
                        doubles[i] = values.get(i).getDoubleValue();
                    }
                    if (!values.isEmpty()) {
                        // final Entity targetEntity = EntityServiceFactory.getInstance().getEntityByUUID(summary.getTargetPointUUID());
                      //  final Point target = PointServiceFactory.getInstance().getPointByKey(summary.getTargetPointUUID());
                        final Point target = (Point) EntityServiceFactory.getInstance().getEntityByKey(summary.getTargetPointUUID(), PointEntity.class.getName());

                        final double result = getValue(summary.getSummaryType(), doubles);
                        final Value value = ValueModelFactory.createValueModel(result);

                        RecordedValueServiceFactory.getInstance().recordValue(user, target, value, false);

                        SummaryServiceFactory.getInstance().updateLastProcessed(entity);
                    }
                } catch (NimbitsException e) {
                    log.severe(e.getMessage());
                    if (user != null) {
                        FeedServiceFactory.getInstance().postToFeed(user, e);
                    }

                }

            }
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }
    }

    protected static double getValue(final SummaryType type, double[] doubles) {
        DescriptiveStatistics d = new DescriptiveStatistics(doubles);

        switch (type) {

            case average:
                return d.getMean();
            case standardDeviation:
                return d.getStandardDeviation();
            case max:
                return d.getMax();
            case min:
                return d.getMin();
            case skewness:
                return d.getSkewness();
            case sum:
                return d.getSum();
            case variance:
                return d.getVariance();

            default:
                return 0;
        }
    }




}
