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
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:08 AM
 */
@Service("summaryService")

public class SummaryService   {
    private static final Logger log = Logger.getLogger(SummaryService.class.getName());


    public static void processSummaries(final User user,final  Point point) {
        final List<Entity> list = EntityServiceImpl.getEntityByTrigger(user, point, EntityType.summary);

        log.info("processing " + list.size() + " summaries");

        for (final Entity entity : list) {
            final Date now = new Date();
            final Summary summary = (Summary) entity;
            final long d = new Date().getTime() - summary.getSummaryIntervalMs();
            log.info(summary.toString());
            if (summary.getLastProcessed().getTime() + summary.getSummaryIntervalMs() < new Date().getTime()) {



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
                                log.info("result:: " + result);
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
                else {
                        log.info("ignored source point not found");
                    }

            }
            else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:S");
                String sd = sdf.format(summary.getLastProcessed());
                String ed = sdf.format(new Date(d));
                String lp = sdf.format(summary.getLastProcessed());
                String next = sdf.format(new Date( summary.getLastProcessed().getTime() + summary.getSummaryIntervalMs()));
                log.info("ignored by timespan setting:: " + sd + ">" + ed + "  " + summary.getSummaryIntervalMs());
                log.info("next:: " + next);
                log.info("last:: " + lp);
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
