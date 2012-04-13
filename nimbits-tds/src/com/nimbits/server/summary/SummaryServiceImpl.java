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

package com.nimbits.server.summary;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.summary.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.value.*;
import org.apache.commons.math3.stat.descriptive.*;

import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:08 AM
 */
public class SummaryServiceImpl  extends RemoteServiceServlet implements SummaryService {


    @Override
    public void processSummaries(User user, Point point) throws NimbitsException {
        List<Summary> list = SummaryTransactionFactory.getInstance().readSummariesToEntity(point);
        for (Summary summary : list) {
            final Date now = new Date();
            final long d = new Date().getTime() - summary.getSummaryIntervalMs();
            if (summary.getLastProcessed().getTime() < d) {

                try {

                    final List<Entity> results =  EntityServiceFactory.getInstance().getEntityByKey(summary.getEntity(), PointEntity.class.getName());
                    if (! results.isEmpty()) {
                        final Entity source = results.get(0);
                        final Timespan span = TimespanModelFactory.createTimespan(new Date(now.getTime() - summary.getSummaryIntervalMs()), now);
                        final List<Value> values = RecordedValueServiceFactory.getInstance().getDataSegment(source, span);


                        if (!values.isEmpty()) {
                            final double[] doubles = new double[values.size()];
                            for (int i = 0; i< values.size(); i++) {
                                doubles[i] = values.get(i).getDoubleValue();
                            }
                            final List<Entity> targetResults =  EntityServiceFactory.getInstance().getEntityByKey(summary.getTarget(), PointEntity.class.getName());
                            if (! targetResults.isEmpty()) {
                                final Entity target = targetResults.get(0);
                                final double result = getValue(summary.getSummaryType(), doubles);
                                final Value value = ValueModelFactory.createValueModel(result);

                                RecordedValueServiceFactory.getInstance().recordValue(user, target, value, false);
                                summary.setLastProcessed(new Date());
                                EntityServiceFactory.getInstance().addUpdateEntity(user, summary);

                            }
                        }
                    }
                } catch (NimbitsException e) {
                    LogHelper.logException(this.getClass(), e);


                }

            }
        }

    }
    @Override
    public double getValue(final SummaryType type, final double[] doubles) {
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
