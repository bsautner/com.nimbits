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

package com.nimbits.cloudplatform.server.transactions.summary;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.SummaryType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.summary.Summary;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.timespan.TimespanModelFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:08 AM
 */
@Service("summaryService")

public class SummaryService   {



    public static void processSummaries(final User user,final  Point point) {
        final List<Entity> list = EntityServiceImpl.getEntityByTrigger(user, point, EntityType.summary);


        for (final Entity entity : list) {
            final Date now = new Date();
            Summary summary = (Summary) entity;
            final long d = new Date().getTime() - summary.getSummaryIntervalMs();
            if (summary.getLastProcessed().getTime() < d) {

                try {

                    final List<Entity> results =  EntityServiceImpl.getEntityByKey(user, summary.getTrigger(), EntityType.point);
                    if (! results.isEmpty()) {
                        final Entity source = results.get(0);
                        final Timespan span = TimespanModelFactory.createTimespan(new Date(now.getTime() - summary.getSummaryIntervalMs()), now);


                        final Value value;
                        if (summary.getSummaryType().equals(SummaryType.delta)) {
                            Point pointSource = ((Point)source);
                            pointSource.setDeltaSeconds(summary.getSummaryIntervalSeconds());
                            double delta = ValueTransaction.calculateDelta(pointSource);
                            value = ValueFactory.createValueModel(delta);

                        }
                        else {
                            final List<Value> values = ValueTransaction.getDataSegment(source, span);
                            if (!values.isEmpty()) {
                                final double[] doubles = new double[values.size()];
                                for (int i = 0; i< values.size(); i++) {
                                    doubles[i] = values.get(i).getDoubleValue();
                                }

                                final double result = getValue(summary.getSummaryType(), doubles);
                                value = ValueFactory.createValueModel(result);
                            }
                            else {
                                return; //nothing to do
                            }
                        }


                        final List<Entity> targetResults =  EntityServiceImpl.getEntityByKey(user, summary.getTarget(), EntityType.point);
                        if (! targetResults.isEmpty()) {
                            final Entity target = targetResults.get(0);
                            ValueTransaction.recordValue(user, target, value);
                            summary.setLastProcessed(new Date());
                            EntityServiceImpl.addUpdateEntity(user, Arrays.<Entity>asList(summary));

                        }

                    }




                } catch (Exception e) {
                    summary.setEnabled(false);
                    EntityServiceImpl.addUpdateEntity(user, Arrays.<Entity>asList(summary));



                }

            }
        }

    }

    public static double getValue(final SummaryType type, final double[] doubles) {
        final DescriptiveStatistics d = new DescriptiveStatistics(doubles);

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
